package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.ServiceGraph;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.capability.OsCapability;

import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.opentosca.toscana.model.capability.OsCapability.Architecture.POWER_PC;
import static org.opentosca.toscana.model.capability.OsCapability.Distribution.DEBIAN;
import static org.opentosca.toscana.model.capability.OsCapability.Distribution.RHEL;
import static org.opentosca.toscana.model.capability.OsCapability.Distribution.UBUNTU;
import static org.opentosca.toscana.model.capability.OsCapability.Type.LINUX;
import static org.opentosca.toscana.model.capability.OsCapability.Type.WINDOWS;

@RunWith(Parameterized.class)
public class MapperErrorTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperErrorTest.class);
    private static BaseImageMapper baseImageMapper;
    private static EntityId entityId = new EntityId(Lists.newArrayList("my", "id"));
    private static MappingEntity entity = new MappingEntity(entityId, new ServiceGraph());

    private String name;
    private OsCapability capability;
    private Class<? extends Exception> expectedException;

    public MapperErrorTest(
        String name,
        OsCapability capability,
        Class<? extends Exception> expectedException
    ) {
        this.name = name;
        this.capability = capability;
        this.expectedException = expectedException;
    }

    @Test
    public void execute() throws Exception {
        try {
            logger.info("Executing test {}", name);
            logger.info("Trying to map {}", capability.toString());
            baseImageMapper.mapToBaseImage(capability);
        } catch (Throwable e) {
            logger.info("Thrown exception (was expected)", e);
            assertTrue(
                "Thrown exception is not of type: " + expectedException.getName(),
                expectedException.isInstance(e)
            );
            return;
        }
        fail();
    }

    @BeforeClass
    public static void initBaseImageMapper() throws Exception {
        logger.info("Initializing Base image Mapper");
        baseImageMapper = MapperTest.init();
    }

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]
            {
                {
                    "Invalid Type",
                    new OsCapability(entity).setType(WINDOWS),
                    UnsupportedOperationException.class
                },
                {
                    "Missing Type, Invalid Distribution",
                    new OsCapability(entity).setDistribution(RHEL),
                    UnsupportedOperationException.class
                },
                {
                    "Vaild Type, Invalid Distribution",
                    new OsCapability(entity).setType(LINUX).setDistribution(RHEL),
                    UnsupportedOperationException.class
                },
                {
                    "Vaild Type, Valid Distribution, Unknown Version",
                    new OsCapability(entity).setDistribution(DEBIAN).setType(LINUX).setVersion("123456"),
                    UnsupportedOperationException.class
                },
                {
                    "Vaild Type, Valid Distribution, Unknown Version(With dots)",
                    new OsCapability(entity).setDistribution(DEBIAN).setType(LINUX).setVersion("12345.6"),
                    UnsupportedOperationException.class
                },
                {
                    "Vaild Type, Valid Distribution, No Applicable minor version",
                    new OsCapability(entity).setType(LINUX).setDistribution(UBUNTU).setVersion("17.11"),
                    UnsupportedOperationException.class
                },
                {
                    "Vaild Type, Valid Distribution, Invalid Architecture",
                    new OsCapability(entity).setArchitecture(POWER_PC).setType(LINUX).setDistribution(UBUNTU),
                    UnsupportedOperationException.class
                }
            }
        );
    }
}
