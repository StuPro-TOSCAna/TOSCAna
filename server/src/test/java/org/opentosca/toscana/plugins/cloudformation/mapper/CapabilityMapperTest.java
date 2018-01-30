package org.opentosca.toscana.plugins.cloudformation.mapper;

import java.text.ParseException;
import java.util.Collection;
import java.util.Optional;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper.EC2_DISTINCTION;
import static org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper.RDS_DISTINCTION;

@RunWith(Parameterized.class)
public class CapabilityMapperTest {
    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapperTest.class);
    private String expectedEC2;
    private String expectedRDS;
    private int expectedDiskSize;
    private CapabilityMapper capabilityMapper;
    private ContainerCapability containerCapability;
    private OsCapability osCapability;
    @Mock
    private Log log;

    public CapabilityMapperTest(Integer numCpus, Integer memSize, Integer diskSize, String expectedEC2, String
        expectedRDS, Integer expectedDiskSize) {
        initMocks(this);
        when(log.getLogger(any(Class.class))).thenReturn(LoggerFactory.getLogger("Test logger"));
        this.expectedEC2 = expectedEC2;
        this.expectedRDS = expectedRDS;
        this.expectedDiskSize = expectedDiskSize;
        logger.debug("{}, {}, {}, {}, {}, {}", numCpus, memSize, diskSize, expectedEC2, expectedRDS, expectedDiskSize);
        this.capabilityMapper = new CapabilityMapper("us-west-2", new BasicAWSCredentials("", ""), logger);
        EffectiveModel singleCompute = new EffectiveModelFactory().create(TestCsars.VALID_SINGLE_COMPUTE_UBUNTU_TEMPLATE, log);
        Compute compute = (Compute) singleCompute.getNodeMap().get("server");
        //containerCapability = compute.getHost();
        containerCapability = mock(ContainerCapability.class);
        when(containerCapability.getDiskSizeInMb()).thenReturn(Optional.of(diskSize));
        when(containerCapability.getMemSizeInMb()).thenReturn(Optional.of(memSize));
        when(containerCapability.getNumCpus()).thenReturn(Optional.of(numCpus));
        osCapability = compute.getOs();
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
        /*
        containerCapability.setNumCpus(numCpus);
        containerCapability.setMemSizeInMb(memSize);
        containerCapability.setDiskSizeInMb(diskSize);
        //.setDiskSizeInMb(20000); produces an error when doing .getDiskSizeInMb()
        */
    }

    @Test
    public void testMapOsCapabilityToImageId() throws ParseException {
        // This is useless because the connection fails everytime because no credentials are set but maybe it will 
        // become valid if we test with credentials
        try {
            String imageId = capabilityMapper.mapOsCapabilityToImageId(osCapability);
            Assert.assertThat(imageId, CoreMatchers.containsString("ami-"));
        } catch (SdkClientException se) {
            logger.info("Probably no internet connection / credentials, omitting test");
        }
    }

    @Test
    public void testMapComputeCapabilityToInstanceTypeEC2() {
        String instanceType = capabilityMapper.mapComputeCapabilityToInstanceType(containerCapability, EC2_DISTINCTION);
        Assert.assertEquals(instanceType, expectedEC2);
    }

    @Test
    public void testMapComputeCapabilityToInstanceTypeRDS() {
        String instanceType = capabilityMapper.mapComputeCapabilityToInstanceType(containerCapability, RDS_DISTINCTION);
        Assert.assertEquals(instanceType, expectedRDS);
    }

    @Test
    public void testMapComputeCapabilityToRDSAllocatedStorage() {
        int newDiskSize = capabilityMapper.mapComputeCapabilityToRDSAllocatedStorage(containerCapability);
        Assert.assertEquals(newDiskSize, expectedDiskSize);
    }
}
