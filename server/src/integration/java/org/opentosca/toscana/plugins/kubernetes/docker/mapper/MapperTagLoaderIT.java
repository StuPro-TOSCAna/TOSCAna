package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import org.opentosca.toscana.IntegrationTest;
import org.opentosca.toscana.core.BaseSpringIntegrationTest;
import org.opentosca.toscana.core.util.Preferences;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.model.capability.OsCapability.Distribution.DEBIAN;
import static org.opentosca.toscana.model.capability.OsCapability.builder;

@ActiveProfiles( {"bimloader"})
@ContextConfiguration(classes = {MapperTagLoaderIT.BIMTestConfiguration.class})
@Category(IntegrationTest.class)
public class MapperTagLoaderIT extends BaseSpringIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperTagLoaderIT.class);

    @Autowired
    private BaseImageMapper mapper;

    @Test
    public void testLoader() throws Exception {
        String image = mapper.mapToBaseImage(
            builder().distribution(DEBIAN).build()
        );
        logger.info("Performed a mapping to {}", image);
        TagStorage data = mapper.getTagStorage();
        assertEquals(DockerBaseImages.values().length, data.size());
    }

    @Profile("bimloader")
    @Configuration
    @EnableScheduling
    public static class BIMTestConfiguration {

        @Bean
        public DockerBaseImages[] getDockerBaseImages() {
            return DockerBaseImages.values();
        }

        @Bean
        public BaseImageMapper getBaseImageMapper(@Autowired DockerBaseImages[] img, @Autowired Preferences preferences) {
            return new BaseImageMapper(img, new TagStorage(preferences));
        }
    }
}
