package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.IntegrationTest;
import org.opentosca.toscana.core.BaseTest;
import org.opentosca.toscana.core.CoreConfiguration;
import org.opentosca.toscana.core.Main;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.capability.OsCapability;

import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.opentosca.toscana.core.testdata.TestProfiles.INTEGRATION_TEST_PROFILE;
import static org.opentosca.toscana.model.capability.OsCapability.Distribution.DEBIAN;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTagLoaderIT.LOADER_TEST_PROFILE;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = {Main.class, CoreConfiguration.class, MapperTagLoaderIT.BIMTestConfiguration.class})
@ActiveProfiles({INTEGRATION_TEST_PROFILE, LOADER_TEST_PROFILE})
@Category(IntegrationTest.class)
public class MapperTagLoaderIT extends BaseTest {

    static final String LOADER_TEST_PROFILE = "base-image-mapper-loader-test";
    private static final Logger logger = LoggerFactory.getLogger(MapperTagLoaderIT.class);
    private static EntityId entityId = new EntityId(Lists.newArrayList("my", "id"));
    private static File logFile;
    private static Log log;
    private static MappingEntity entity;

    @Autowired
    private BaseImageMapper mapper;

    @BeforeClass
    public static void setUp() throws IOException {
        logFile = File.createTempFile("testlog", "log", PROJECT_ROOT);
        logFile.deleteOnExit();
        log = new LogImpl(logFile);
        entity = new MappingEntity(entityId, new ServiceGraph(log));
    }

    @Test
    public void testLoader() {
        String image = mapper.mapToBaseImage(
            new OsCapability(entity).setDistribution(DEBIAN)
        );
        logger.info("Performed a mapping to {}", image);
        TagStorage data = mapper.getTagStorage();
        assertEquals(DockerBaseImages.values().length, data.size());
    }

    @Profile(LOADER_TEST_PROFILE)
    @TestConfiguration
    @EnableScheduling
    public static class BIMTestConfiguration {

        @Bean
        public DockerBaseImages[] getDockerBaseImages() {
            return DockerBaseImages.values();
        }

        @Bean
        @Primary
        public BaseImageMapper getBaseImageMapper(@Autowired DockerBaseImages[] img, @Autowired Preferences preferences) {
            new File(preferences.getDataDir(), TagStorage.DOCKER_IMAGE_TAGS).delete();
            return new BaseImageMapper(img, new TagStorage(preferences));
        }
    }
}
