package org.opentosca.toscana.plugins.cloudformation.mapper;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper.EC2_DISTINCTION;
import static org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper.RDS_DISTINCTION;

@RunWith(Parameterized.class)
public class CapabilityMapperTest extends BaseUnitTest {
    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapperTest.class);
    private int numCpus;
    private int memSize;
    private int diskSize;
    private String expectedEC2;
    private String expectedRDS;
    private int expectedDiskSize;
    private CapabilityMapper capabilityMapper;

    public CapabilityMapperTest(Integer numCpus, Integer memSize, Integer diskSize, String expectedEC2, String
        expectedRDS, Integer expectedDiskSize) throws ParseException {
        this.numCpus = numCpus;
        this.memSize = memSize;
        this.diskSize = diskSize;
        this.expectedEC2 = expectedEC2;
        this.expectedRDS = expectedRDS;
        this.expectedDiskSize = expectedDiskSize;
        logger.debug("{}, {}, {}, {}, {}, {}", numCpus, memSize, diskSize, expectedEC2, expectedRDS, expectedDiskSize);
        this.capabilityMapper = new CapabilityMapper("us-west-2", new BasicAWSCredentials("", ""));
    }

    @Parameters
    public static Collection data() {
        return asList(new Object[][]{
            {1, 1024, 20000, "t2.micro", "db.t2.micro", 20}, {1, 2048, 40000, "t2.small", "db.t2.small", 40}, {2,
            1024, 6144001, "t2.medium", "db" +
            ".t2.medium", 6144}, {3, 7500, 3, "t2.xlarge", "db.t2.xlarge", 20}, {1, 5000, 10000, "t2.large", "db" +
            ".t2.large", 20}, {1, 17000, 100000, "t2.2xlarge", "db.t2.2xlarge", 100}
        });
    }

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testMapOsCapabilityToImageId() throws ParseException {
        // This is useless because it returns "ami-testami" everytime, but maybe it will become valid if we test with
        // credentials
        try {
            String imageId = capabilityMapper.mapOsCapabilityToImageId(createOSCapability());
            Assert.assertThat(imageId, CoreMatchers.containsString("ami-"));
        } catch (SdkClientException se) {
            logger.info("Probably no internet connection, omitting test");
        }
    }

    @Test
    public void testMapComputeCapabilityToInstanceTypeEC2() {
        String instanceType = capabilityMapper.mapComputeCapabilityToInstanceType(createContainerCapability(numCpus,
            memSize, diskSize), EC2_DISTINCTION);
        Assert.assertEquals(instanceType, expectedEC2);
    }

    @Test
    public void testMapComputeCapabilityToInstanceTypeRDS() {
        String instanceType = capabilityMapper.mapComputeCapabilityToInstanceType(createContainerCapability(numCpus,
            memSize, diskSize), RDS_DISTINCTION);
        Assert.assertEquals(instanceType, expectedRDS);
    }

    @Test
    public void testMapComputeCapabilityToRDSAllocatedStorage() {
        int newDiskSize = capabilityMapper.mapComputeCapabilityToRDSAllocatedStorage(createContainerCapability
            (numCpus,
                memSize, diskSize));
        Assert.assertEquals(newDiskSize, expectedDiskSize);
    }

    private ContainerCapability createContainerCapability(Integer numCpus, Integer memSize, Integer diskSize) {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        validSourceTypes.add(MysqlDbms.class);

        ContainerCapability.ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(memSize)
            .diskSizeInMB(diskSize)
            .numCpus(numCpus)
            .validSourceTypes(validSourceTypes);

        return containerCapabilityBuilder.build();
    }

    private OsCapability createOSCapability() {
        return OsCapability
            .builder()
            .distribution(OsCapability.Distribution.UBUNTU)
            .type(OsCapability.Type.LINUX)
            .version("16.04")
            .build();
    }
}
