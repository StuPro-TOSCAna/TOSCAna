package org.opentosca.toscana.plugins.cloudformation;

import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;
import org.opentosca.toscana.plugins.testdata.TestEffectiveModels;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudFormationPluginTest extends BaseUnitTest {
    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPluginTest.class);
    private final static EffectiveModel lamp = new TestEffectiveModels().getLampModel();
    private static CloudFormationNodeVisitor cfnNodeVisitor;
    private static CloudFormationModule cfnModule = new CloudFormationModule();
    
    @Before
    public void setUp() throws Exception{
        cfnNodeVisitor = new CloudFormationNodeVisitor(logger, cfnModule);
    }
    
    @Test
    public void testLamp(){
        Set<RootNode> nodes = lamp.getNodes();
        for (VisitableNode node : nodes) {
            node.accept(cfnNodeVisitor);
        }
        System.err.println(cfnModule.toString());
    }
}
