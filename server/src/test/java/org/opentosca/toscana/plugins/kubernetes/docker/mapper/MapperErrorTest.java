package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Arrays;
import java.util.Collection;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.capability.OsCapability;

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
import static org.opentosca.toscana.model.capability.OsCapability.builder;

@RunWith(Parameterized.class)
public class MapperErrorTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(MapperErrorTest.class);
    private static BaseImageMapper baseImageMapper;

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
                        builder().type(WINDOWS).build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Missing Type, Invalid Distribution",
                        builder().distribution(RHEL).build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Vaild Type, Invalid Distribution",
                        builder().type(LINUX).distribution(RHEL).build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Vaild Type, Valid Distribution, Unknown Version",
                        builder().distribution(DEBIAN).type(LINUX).version("123456").build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Vaild Type, Valid Distribution, Unknown Version(With dots)",
                        builder().distribution(DEBIAN).type(LINUX).version("12345.6").build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Vaild Type, Valid Distribution, No Applicable minor version",
                        builder().type(LINUX).distribution(UBUNTU).version("17.11").build(),
                        UnsupportedOperationException.class
                    },
                    {
                        "Vaild Type, Valid Distribution, Invalid Architecture",
                        builder().architecture(POWER_PC).type(LINUX).distribution(UBUNTU).build(),
                        UnsupportedOperationException.class
                    }
                }
        );
    }
}
