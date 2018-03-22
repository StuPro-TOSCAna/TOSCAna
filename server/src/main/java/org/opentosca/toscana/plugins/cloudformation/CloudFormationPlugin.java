package org.opentosca.toscana.plugins.cloudformation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.auth.profile.internal.BasicProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 This is the CloudFormation plugin which performs the transformation to AmazonWebServices CloudFormation.
 It uses the {@link CloudFormationLifecycle} to perform its task

 @see <a href="https://aws.amazon.com/cloudformation/">AWS CloudFormation</a> 
 */
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
        /* Try to get AWS credentials from the system. */
        try {
            ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
            AWSCredentials awsCredentials = profileCredentialsProvider.getCredentials();
            defaultKeyId = awsCredentials.getAWSAccessKeyId();
            defaultKeySecret = awsCredentials.getAWSSecretKey();
        } catch (Exception e) {
            logger.debug("Did not find credentials on the system");
        }
        String defaultRegion = AWS_REGION_DEFAULT;
        /* Try to get AWS region from the system. */
        try {
            ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile();
            Map<String, BasicProfile> basicProfiles = profilesConfigFile.getAllBasicProfiles();
            BasicProfile defaultProfile = basicProfiles.get("default");
            if (defaultProfile.getRegion() != null) {
                defaultRegion = defaultProfile.getRegion();
            }
        } catch (Exception e) {
            logger.debug("Did not find region on the system");
        }
        platformProperties.add(new PlatformInput(
                AWS_REGION_KEY,
                PropertyType.TEXT,
                "The AWS Region where the transformed model will run in. (The image id of possible EC2 machines depends on this)",
                true,
                defaultRegion
            )
        );
        platformProperties.add(new PlatformInput(
                AWS_ACCESS_KEY_ID_KEY,
                PropertyType.TEXT,
                "Your access key id",
                true,
                defaultKeyId
            )
        );
        platformProperties.add(new PlatformInput(
                AWS_SECRET_KEY_KEY,
                PropertyType.SECRET,
                "Your access key secret",
                true,
                defaultKeySecret
            )
        );
        platformProperties.add(new PlatformInput(
                AWS_KEYPAIR_KEY,
                PropertyType.BOOLEAN,
                "If enabled, adds a AWS 'Keypair' Parameter to the template in order to access EC2 Instances via SSH. Must be specified during Deployment.",
                true,
                "false"
            )
        );
        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    public CloudFormationLifecycle getInstance(TransformationContext context) throws Exception {
        return new CloudFormationLifecycle(context);
    }
}
