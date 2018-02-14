package org.opentosca.toscana.plugins.cloudformation;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFormationPlugin extends ToscanaPlugin<CloudFormationLifecycle> {
    public static final String AWS_REGION_KEY = "AWS Region";
    public static final String AWS_REGION_DEFAULT = "us-west-2";
    public static final String AWS_ACCESS_KEY_ID_KEY = "AWS Access Key ID";
    public static final String AWS_SECRET_KEY_KEY = "AWS Secret Key";
    public static final String AWS_KEYPAIR_KEY = "Add AWS KeyPair to template";

    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPlugin.class);

    public CloudFormationPlugin() {
        super(getPlatformDetails());
    }

    private static Platform getPlatformDetails() {
        String platformId = "cloudformation";
        String platformName = "AWS CloudFormation";
        Set<PlatformInput> platformProperties = new HashSet<>();
        String defaultKeyId = "";
        String defaultKeySecret = "";
        try {
            ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
            AWSCredentials awsCredentials = profileCredentialsProvider.getCredentials();
            defaultKeyId = awsCredentials.getAWSAccessKeyId();
            defaultKeySecret = awsCredentials.getAWSSecretKey();
        } catch (Exception e) {
            logger.debug("Did not find credentials on the system");
        }
        String defaultRegion = AWS_REGION_DEFAULT;
        platformProperties.add(new PlatformInput(
            AWS_REGION_KEY,
            PropertyType.TEXT,
            "The AWS Region this should be transformed to. (The imageId of possible EC2 machines depend on this)",
            true,
            defaultRegion
        ));
        platformProperties.add(new PlatformInput(
            AWS_ACCESS_KEY_ID_KEY,
            PropertyType.TEXT,
            "The Access key id",
            true,
            defaultKeyId
        ));
        platformProperties.add(new PlatformInput(
            AWS_SECRET_KEY_KEY,
            PropertyType.SECRET,
            "The Access key secret",
            true,
            defaultKeySecret
        ));
        
        platformProperties.add(new PlatformInput(
            AWS_KEYPAIR_KEY,
            PropertyType.BOOLEAN,
            "If enabled, adds a AWS 'Keypair' Parameter to the template in order to access EC2 Instances via SSH. Must be specified during Deplyoment.",
            true,
            "false"
        ));
        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    public CloudFormationLifecycle getInstance(TransformationContext context) throws Exception {
        return new CloudFormationLifecycle(context);
    }
}
