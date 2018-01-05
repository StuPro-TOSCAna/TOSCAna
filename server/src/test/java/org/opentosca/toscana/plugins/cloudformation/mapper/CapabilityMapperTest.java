package org.opentosca.toscana.plugins.cloudformation.mapper;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;

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
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class CapabilityMapperTest extends BaseUnitTest {
    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapperTest.class);
    private static EffectiveModel singleComputeNode;
    private static CloudFormationNodeVisitor cfnNodeVisitor;
    private static CloudFormationModule cfnModule;
    private static PluginFileAccess fileAccess;
    @Mock
    private Log log;

    private Integer numCpus;
    private Integer memSize;
    private String expectedResult;

    public CapabilityMapperTest(Integer numCpus, Integer memSize, String expectedResult) {
        this.numCpus = numCpus;
        this.memSize = memSize;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection data() {
        return asList(new Object[][]{
            {1, 1024, "t2.micro"}, {1, 2048, "t2.small"}
        });
    }

    @Before
    public void setUp() throws Exception {
        when(log.getLogger(any(Class.class))).thenReturn(LoggerFactory.getLogger("Test logger"));
        fileAccess = new PluginFileAccess(new File("src/test/resources/csars/yaml/valid/lamp-input/"), tmpdir, log);
        cfnModule = new CloudFormationModule(fileAccess);
        cfnNodeVisitor = new CloudFormationNodeVisitor(logger, cfnModule);
    }

    @Test
    public void testSingle() {
        Set<RootNode> computeNodeSet = new HashSet<>();
        computeNodeSet.add(createComputeNode(numCpus, memSize));
        singleComputeNode = new EffectiveModel(computeNodeSet);
        Set<RootNode> nodes = singleComputeNode.getNodes();
        for (VisitableNode node : nodes) {
            node.accept(cfnNodeVisitor);
        }
        String template = cfnModule.toString();
        Assert.assertThat(template, CoreMatchers.containsString(expectedResult));
        System.err.println(template);
    }

    private Compute createComputeNode(Integer numCpus, Integer memSize) {
        OsCapability osCapability = OsCapability
            .builder()
            .distribution(OsCapability.Distribution.UBUNTU)
            .type(OsCapability.Type.LINUX)
            .version("16.04")
            .build();
        Compute computeNode = Compute
            .builder("server")
            .os(osCapability)
            .host(createContainerCapability(numCpus, memSize))
            .build();
        return computeNode;
    }

    private ContainerCapability createContainerCapability(Integer numCpus, Integer memSize) {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        validSourceTypes.add(MysqlDbms.class);

        ContainerCapability.ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(memSize)
            .diskSizeInMB(2000)
            .numCpus(numCpus)
            .validSourceTypes(validSourceTypes);

        return containerCapabilityBuilder.build();
    }
}
