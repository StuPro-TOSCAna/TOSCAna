package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.opentosca.toscana.core.Profiles;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.DockerRegistry;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model.Image;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model.ImageTag;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model.ImageTags;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImage;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImageTag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 This class allows the mapping of OsCapabilities (from a tosca model) to a docker base image.
 When initialized with Spring this class will automatically download the latest tags from the base images
 defined in the <code>DockerBaseImages</code> every 24 hours (by default).
 To update this value please modify the <code>toscana.docker.base-image-mapper.update-interval</code> property (value in
 hours)
 */
@Service
@Profile("!" + Profiles.EXCLUDE_BASE_IMAGE_MAPPER)
@SuppressWarnings("ConstantConditions")
public class BaseImageMapper {

    public static final String DOCKER_IMAGE_DIR = "misc/docker-imagetags";
    private static final Logger logger = LoggerFactory.getLogger(BaseImageMapper.class);

    private final File dataDir;
    private final File imageTagsFile;
    private final File lastUpdateFile;

    private List<ImageTags> imageTags;

    /**
     Stores the update interval for the base Image mappings (in hours)
     Taken from the property value:
     <code>toscana.docker.base-image-mapper.update-interval</code>
     */
    @Value("${toscana.docker.base-image-mapper.update-interval}")
    private int updateInterval;

    /**
     This field is used to check the last update timestamp
     */
    private long lastUpdate;
    /**
     The map containing the "raw" tag data collected from dockerhub
     */
    private Map<String, DockerImage> imageMap = new HashMap<>();

    /**
     A array conatinig the base images that this Base Image Mapper tries to map to
     this usually is <code>DockerBaseImages.values()</code>
     */
    private final DockerBaseImages[] baseImages;

    private MapperEngine engine;

    @Autowired
    public BaseImageMapper(DockerBaseImages[] dockerBaseImages, Preferences preferences) {
        this.baseImages = dockerBaseImages;
        engine = new MapperEngine(imageMap);
        dataDir = new File(preferences.getDataDir(), DOCKER_IMAGE_DIR);
        dataDir.mkdirs();
        imageTagsFile = new File(dataDir, "image_tags");
        lastUpdateFile = new File(dataDir, "last_update");
        deserializeTags();

    }

    private void deserializeTags() {
        try {
            if (imageTagsFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(imageTagsFile));
                Object o = ois.readObject();
                ois.close();
                if (o instanceof List) {
                    imageTags = (List<ImageTags>) o;
                } else {
                    logger.warn("File '{}' contained wrong data. Deleting file", imageTagsFile);
                }
            }
            if (lastUpdateFile.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(lastUpdateFile));
                lastUpdate = ois.readLong();
                ois.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Failed to deserialize docker tag information from disk. Removing files", e);
            imageTagsFile.delete();
            lastUpdateFile.delete();
        }
    }

    /**
     Toggles the first update of the mappings during initialisation in spring
     */
    @PostConstruct
    private void postConstruct() {
        updateBaseImageMap();
    }

    /**
     Performs the Update of each image and sets the "last update" timestamp at the end
     */
    private void updateBaseImageMap() {
        logger.info("Updating BaseImage Mappings");
        for (DockerBaseImages baseImage : baseImages) {
            logger.debug("Fetching tags for base image {}", baseImage.name());
            if (imageTags == null) {
                imageTags = fetchImageTags(baseImage);
                persistTags(imageTags);
            }
            if (imageTags != null) {
                logger.debug("Remapping Tags for Base image {}", baseImage.name());
                addImagesForType(baseImage, imageTags);
            }
        }
        setLastUpdateNow();
        logger.info("Mappings have been updated. The next update will be executed in approx. {} hours", updateInterval);
    }

    private void persistTags(List<ImageTags> tags) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(imageTagsFile));
            oos.writeObject(tags);
            oos.close();
        } catch (IOException e) {
            logger.warn("Failed to write most recent docker image tags to file '{}", imageTagsFile, e);
        }

    }

    private void setLastUpdateNow() {
        lastUpdate = System.currentTimeMillis();
        try {
            lastUpdateFile.delete();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(lastUpdateFile));
            oos.writeLong(lastUpdate);
            oos.close();
        } catch (IOException e) {
            logger.warn("Failed to write most recent update time of docker image tags to file '{}", lastUpdateFile, e);
        }
    }


    /**
     This is the spring scheduled job used to perform the updates on a regular basis
     <p>
     This method gets called every 10 minutes and if the Current timestamp is larger than the old one plus the time
     period
     it triggers a update of the mapping tables
     */
    @Scheduled(fixedRate = 600000)
    private void updateCronjob() {
        long time = System.currentTimeMillis();
        if (time >= (lastUpdate + updateInterval * 3600 * 1000)) {
            updateBaseImageMap();
        }
    }

    /**
     Internal method used for converting the Data received from docker to the data model described
     in the <code>model</code> package
     */
    private void addImagesForType(DockerBaseImages baseImage, List<ImageTags> pages) {
        List<DockerImageTag> tagList = new ArrayList<>();
        for (ImageTags page : pages) {
            for (ImageTag imageTag : page.getImageTags()) {
                Set<String> architectures = new HashSet<>();
                for (Image image : imageTag.getImages()) {
                    //Because of older versions the architecture might be null, we assume the image
                    //is amd64 in that case.
                    if (image.getArchitecture() == null) {
                        architectures.add("amd64");
                    } else {
                        architectures.add(image.getArchitecture());
                    }
                }
                DockerImageTag tag = new DockerImageTag(imageTag.getName(), architectures);
                tagList.add(tag);
            }
        }
        imageMap.put(baseImage.name().toLowerCase(), new DockerImage(baseImage, tagList));
    }

    protected void setImageMap(Map<String, DockerImage> imageMap) {
        this.imageMap = imageMap;
        this.engine = new MapperEngine(imageMap);
    }

    protected Map<String, DockerImage> getImageMap() {
        return imageMap;
    }

    /**
     Downloads the tags for a given base image
     */
    private List<ImageTags> fetchImageTags(DockerBaseImages image) {
        DockerRegistry registry = new DockerRegistry(image.getRegistry());
        return registry.getTagsForRepository(image.getUsername(), image.getRepository());
    }

    /**
     This method attempts to map a OsCapability to a docker base image.
     If the mapping fails a UnsupportedOperationException is thrown. Reasons for failure are: Invalid Architecture,
     Unsupported type, Unknown version...
     */
    public String mapToBaseImage(OsCapability capability) {
        return engine.mapToBaseImage(capability);
    }
}
