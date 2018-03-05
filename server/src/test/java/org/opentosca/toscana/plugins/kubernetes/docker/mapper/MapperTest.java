package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.core.BaseTest;
import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.OsCapability.Distribution;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.util.DataContainer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_64;
import static org.opentosca.toscana.model.capability.OsCapability.Type.LINUX;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages.ALPINE;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages.DEBIAN;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages.UBUNTU;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants.DEFAULT_IMAGE_PATH;

@RunWith(Parameterized.class)
public class MapperTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperTest.class);
    private static BaseImageMapper baseImageMapper;
    private static EntityId entityId = new EntityId(Lists.newArrayList("my", "id"));

    private String name;
    private OsCapability capability;
    private String expectedImage;

    public MapperTest(String name, OsCapability capability, String expectedImage) {
        this.name = name;
        this.capability = capability;
        this.expectedImage = expectedImage;
    }

    @Test
    public void test() throws Exception {
        logger.info("Executing test {}", name);
        String image = baseImageMapper.mapToBaseImage(capability);
        logger.info("Mapped capability {} to {}. Expected is {}", capability.toString(), image, expectedImage);
        assertEquals(expectedImage, image);
    }

    @BeforeClass
    public static void initBaseImageMapper() throws Exception {
        logger.info("Initializing Base image Mapper");

        baseImageMapper = init();
    }

    public static BaseImageMapper init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = BaseImageMapper.class.getClassLoader()
            .getResourceAsStream("kubernetes/base-image-mapper/image-map.json");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        Preferences preferences = mock(Preferences.class);
        when(preferences.getDataDir()).thenReturn(staticTmpDir);
        TagStorage tagStorage = new TagStorage(preferences);
        BaseImageMapper baseImageMapper = new BaseImageMapper(new DockerBaseImages[] {
            ALPINE,
            DEBIAN,
            UBUNTU
        }, tagStorage);
        DataContainer data = mapper.readValue(new String(out.toByteArray()), DataContainer.class);

        baseImageMapper.setImageMap(data.data);

        return baseImageMapper;
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() throws IOException {
        return Arrays.asList(new Object[][]
            {
                {
                    "Empty Capability",
                    new OsCapability(getEntity()),
                    DEFAULT_IMAGE_PATH
                },
                {
                    "Specific Version",
                    new OsCapability(getEntity()).setType(LINUX).setDistribution(Distribution.DEBIAN).setVersion("9.2"),
                    "library/debian:9.2"
                },
                {
                    "Version too specific",
                    new OsCapability(getEntity()).setType(LINUX).setDistribution(Distribution.UBUNTU).setVersion("16.04.3"),
                    "library/ubuntu:16.04"
                },
                {
                    "Version not found but newer fix version available",
                    new OsCapability(getEntity()).setType(LINUX).setDistribution(Distribution.UBUNTU).setVersion("12.04.3"),
                    "library/ubuntu:12.04.5"
                },
                {
                    "Specific Version not found (Expect later version)",
                    new OsCapability(getEntity()).setType(LINUX).setDistribution(Distribution.UBUNTU).setVersion("17.09"),
                    "library/ubuntu:17.10"
                },
                {
                    "Architecture Only",
                    new OsCapability(getEntity()).setArchitecture(x86_64),
                    DEFAULT_IMAGE_PATH
                },
                {
                    "Missing Distribution",
                    new OsCapability(getEntity()).setType(LINUX).setVersion("17.09"),
                    DEFAULT_IMAGE_PATH
                },
            }
        );
    }

    public static MappingEntity getEntity() {
        ServiceGraph graph = new ServiceGraph(BaseTest.logMock());
        MappingEntity entity = new MappingEntity(entityId, graph);
        graph.addEntity(entity);
        return entity;
    }
}
