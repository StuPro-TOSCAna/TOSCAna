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

 @see <a href="https://aws.amazon.com/cloudformation/">AWS CloudFormation</a> */
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
        String[] systemCredentials = getCredentialsFromSystem();
        if (systemCredentials != null) {
            defaultKeyId = systemCredentials[0];
            defaultKeySecret = systemCredentials[1];
        }

        String defaultRegion = AWS_REGION_DEFAULT;
        String systemRegion = getRegionFromSystem();
        if (systemRegion != null) {
            defaultRegion = systemRegion;
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
            "Your AWS Access Key Id",
                true,
                defaultKeyId
            )
        );
        platformProperties.add(new PlatformInput(
                AWS_SECRET_KEY_KEY,
                PropertyType.SECRET,
            "Your AWS Access Key Secret",
                true,
                defaultKeySecret
            )
        );
        platformProperties.add(new PlatformInput(
                AWS_KEYPAIR_KEY,
                PropertyType.BOOLEAN,
            "If enabled, adds an AWS 'Keypair' Parameter to the template in order to access EC2 Instances via SSH. The name of the Keypair must be specified during deployment.",
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

    private static String getRegionFromSystem() {
        try {
            ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile();
            Map<String, BasicProfile> basicProfiles = profilesConfigFile.getAllBasicProfiles();
            BasicProfile defaultProfile = basicProfiles.get("default");
            return defaultProfile.getRegion();
        } catch (Exception e) {
            logger.debug("Did not find region on the system");
            return null;
        }
    }

    private static String[] getCredentialsFromSystem() {
        try {
            ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
            AWSCredentials awsCredentials = profileCredentialsProvider.getCredentials();
            return new String[]{awsCredentials.getAWSAccessKeyId(), awsCredentials.getAWSSecretKey()};
        } catch (Exception e) {
            logger.debug("Did not find credentials on the system");
            return null;
        }
    }
}
