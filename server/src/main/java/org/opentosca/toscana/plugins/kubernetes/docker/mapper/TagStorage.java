package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.core.Profiles;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!" + Profiles.EXCLUDE_BASE_IMAGE_MAPPER)
public class TagStorage {

    public static final String DOCKER_IMAGE_TAGS = "misc/docker-tagbase.json";

    private static final Logger logger = LoggerFactory.getLogger(TagStorage.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();


    private final File file;

    private TagBaseData data = new TagBaseData();

    /**
     Stores the persist interval for the base Image mappings (in hours)
     Taken from the property value:
     <code>toscana.docker.base-image-mapper.persist-interval</code>
     */
    @Value("${toscana.docker.base-image-mapper.update-interval}")
    private int updateInterval;

    public TagStorage(@Autowired Preferences preferences) {
        this.file = new File(preferences.getDataDir(), DOCKER_IMAGE_TAGS);
        file.getParentFile().mkdirs();
        if (file.exists()) {
            load();
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try {
            data = MAPPER.readValue(file, TagBaseData.class);
        } catch (IOException | ClassCastException e) {
            logger.error("Failed to read from '{}'. Deleting file", file, e);
            file.delete();
        }
    }

    void persist() {
        try {
            data.lastUpdateTime = System.currentTimeMillis();
            MAPPER.writeValue(file, data);
            logger.info("Docker mappings saved to disk. Next persist in approx. {} hours", updateInterval);
        } catch (IOException e) {
            logger.error("Failed to save docker mappings to file '{}'", file, e);
        }
    }

    boolean needsUpdate() {
        long currentTime = System.currentTimeMillis();
        long updateIntervalExceeds = data.lastUpdateTime + updateInterval * 3600 * 1000;
        return currentTime >= updateIntervalExceeds;
    }

    /**
     Returns a point in time when the next persist is due.
     */
    Instant getNextUpdate() {
        Duration updateIntervalDuration = Duration.ofHours(updateInterval);
        return Instant.ofEpochMilli(data.lastUpdateTime).plus(updateIntervalDuration);
    }

    public DockerImage get(String distro) {
        return data.map.get(distro);
    }

    public DockerImage put(String distro, DockerImage image) {
        return data.map.put(distro, image);
    }

    public int size() {
        return data.map.size();
    }

    public void putAll(Map<String, DockerImage> imageMap) {
        data.map.putAll(imageMap);
    }

    private static class TagBaseData {

        public long lastUpdateTime = 0;
        public final Map<String, DockerImage> map = new HashMap<>();
    }

}
