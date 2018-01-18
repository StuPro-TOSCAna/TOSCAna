package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CloudFormationPluginTest extends BaseUnitTest {
    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPluginTest.class);
    private static CloudFormationNodeVisitor cfnNodeVisitor;
    private static CloudFormationModule cfnModule;
    private static PluginFileAccess fileAccess;
    @Mock
    private Log log;
    private EffectiveModel lamp;

    @Before
    public void setUp() throws Exception {
        lamp = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE);
        when(log.getLogger(any(Class.class))).thenReturn(LoggerFactory.getLogger("Test logger"));
        fileAccess = new PluginFileAccess(new File("src/test/resources/csars/yaml/valid/lamp-noinput/"), tmpdir, log);
        cfnModule = new CloudFormationModule(fileAccess);
        cfnNodeVisitor = new CloudFormationNodeVisitor(logger, cfnModule);
    }

    @Test
    public void testLamp() {
        Set<RootNode> nodes = lamp.getNodes();
        //visit compute nodes first
        for (VisitableNode node : nodes) {
            if (node instanceof Compute) {
                node.accept(cfnNodeVisitor);
            }
        }
        for (VisitableNode node : nodes) {
            if (!(node instanceof Compute)) {
                node.accept(cfnNodeVisitor);
            }
        }
        System.err.println(cfnModule.toString());
    }
}
