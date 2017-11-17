package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Map;

import org.opentosca.toscana.core.integration.BaseIntegrationTest;

import org.junit.Test;
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
@ContextConfiguration(classes = {MapperLoaderTest.BIMTestConfiguration.class})
public class MapperLoaderTest extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperLoaderTest.class);

    @Autowired
    private BaseImageMapper mapper;

    @Test
    public void testLoader() throws Exception {
        String image = mapper.mapToBaseImage(
            builder().distribution(DEBIAN).build()
        );
        logger.info("Performed a mapping to {}", image);
        Map data = mapper.getImageMap();
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
        public BaseImageMapper getBaseImageMapper(@Autowired DockerBaseImages[] img) {
            return new BaseImageMapper(img);
        }
    }
}
