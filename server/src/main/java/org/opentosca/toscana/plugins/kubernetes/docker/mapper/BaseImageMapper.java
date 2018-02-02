package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.opentosca.toscana.core.Profiles;
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
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 This class allows the mapping of OsCapabilities (from a tosca model) to a docker base image.
 When initialized with Spring this class will automatically download the latest tags from the base images
 defined in the <code>DockerBaseImages</code> every 24 hours (by default).
 To persist this value please modify the <code>toscana.docker.base-image-mapper.persist-interval</code> property (value in
 hours)
 */
@Service
@Profile("!" + Profiles.EXCLUDE_BASE_IMAGE_MAPPER)
public class BaseImageMapper {

    private static final Logger logger = LoggerFactory.getLogger(BaseImageMapper.class);

    /**
     Administrates the "raw" tag data collected from docker-hub
     */
    private TagStorage tagStorage;

    /**
     An array containing the base images that this Base Image Mapper tries to map to
     this usually is <code>DockerBaseImages.values()</code>
     */
    private final DockerBaseImages[] baseImages;

    private MapperEngine engine;

    @Autowired
    public BaseImageMapper(DockerBaseImages[] dockerBaseImages, TagStorage tagStorage) {
        this.baseImages = dockerBaseImages;
        this.tagStorage = tagStorage;
        engine = new MapperEngine(tagStorage);
    }

    /**
     Toggles the first persist of the mappings during initialisation in spring
     */
    @PostConstruct
    private void postConstruct() {
        updateBaseImageMap();
    }

    /**
     Performs the Update of each image and sets the "last persist" timestamp at the end
     */
    private void updateBaseImageMap() {
        if (tagStorage.needsUpdate()) {
            try {
                logger.info("Updating docker base tags");
                for (DockerBaseImages baseImage : baseImages) {
                    logger.debug("Fetching tags for base image {}", baseImage.name());
                    List<ImageTags> imageTags = fetchImageTags(baseImage);
                    logger.debug("Remapping Tags for Base image {}", baseImage.name());
                    addImagesForType(baseImage, imageTags);
                }
                tagStorage.persist();
            } catch (Exception e) {
                logger.error("Persisting the BaseImageTags failed. " +
                    "This could cause issues when perforimin Kubernetes Based Transformations", e);
            }
        } else {
            logger.debug("Not updating docker base tags: Using local data (next persist: {})", tagStorage.getNextUpdate());
        }
    }

    /**
     This is the spring scheduled job used to perform the updates on a regular basis
     <p>
     it triggers a persist of the mapping tables (which gets executed, if an persist is needed)
     */
    @Scheduled(fixedRate = 600000, initialDelay = 600000)
    private void updateCronjob() {
        updateBaseImageMap();
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
        tagStorage.put(baseImage.name().toLowerCase(), new DockerImage(baseImage, tagList));
    }

    protected void setImageMap(Map<String, DockerImage> imageMap) {
        tagStorage.putAll(imageMap);
        this.engine = new MapperEngine(tagStorage);
    }

    protected TagStorage getTagStorage() {
        return tagStorage;
    }

    /**
     Downloads the tags for a given base image
     */
    private List<ImageTags> fetchImageTags(DockerBaseImages image) throws Exception {
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
