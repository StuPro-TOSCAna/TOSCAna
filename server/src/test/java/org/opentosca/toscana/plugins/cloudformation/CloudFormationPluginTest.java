package org.opentosca.toscana.plugins.cloudformation;

import java.io.File;
import java.util.Set;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.VisitableNode;
import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper;
import org.opentosca.toscana.plugins.cloudformation.visitor.CloudFormationNodeVisitor;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CloudFormationPluginTest extends BaseUnitTest {
    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPluginTest.class);
    private static CloudFormationModule cfnModule;
    private static PluginFileAccess fileAccess;
    private static CloudFormationNodeVisitor cfnNodeVisitor;
    private EffectiveModel lamp;

    @Before
    public void setUp() throws Exception {
        lamp = new EffectiveModel(TestCsars.VALID_LAMP_NO_INPUT_TEMPLATE, log);
        fileAccess = new PluginFileAccess(new File("src/test/resources/csars/yaml/valid/lamp-input/"), tmpdir, log);
        cfnModule = new CloudFormationModule(fileAccess, "us-west-2", new BasicAWSCredentials("", ""));
        CloudFormationNodeVisitor cfnNodeVisitorL = new CloudFormationNodeVisitor(logger, cfnModule);
        cfnNodeVisitor = spy(cfnNodeVisitorL);
        CapabilityMapper capabilityMapper = mock(CapabilityMapper.class);
        when(capabilityMapper.mapOsCapabilityToImageId(any(OsCapability.class))).thenReturn("ami-testami");
        when(cfnNodeVisitor.createCapabilityMapper()).thenReturn(capabilityMapper);
    }

    @Test
    public void testLamp() {
        try {
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
        } catch (TransformationFailureException tfe) {
            // if cause is sdkclientexception we assume there is no internet connection/(wrong/no) credentials 
            // provided so this test can pass
            if (!(tfe.getCause() instanceof SdkClientException)) {
                throw tfe;
            }
            logger.debug("Passed without internet connection / credentials provided");
        }
    }
}
