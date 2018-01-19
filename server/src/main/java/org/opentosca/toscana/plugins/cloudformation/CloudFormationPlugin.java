package org.opentosca.toscana.plugins.cloudformation;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.plugins.lifecycle.LifecycleAwarePlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFormationPlugin extends LifecycleAwarePlugin<CloudFormationLifecycle> {
    public static final String AWS_REGION_KEY = "AWS Region";
    public static final String AWS_REGION_DEFAULT = "us-west-2";
    public static final String AWS_KEY_ID_KEY = "AWS Access Key ID";
    public static final String AWS_KEY_SECRET_KEY = "AWS Access Key Secret";
    private final static Logger logger = LoggerFactory.getLogger(CloudFormationPlugin.class);

    public CloudFormationPlugin() {
        super(getPlatformDetails());
    }

    private static Platform getPlatformDetails() {
        String platformId = "cloudformation";
        String platformName = "AWS CloudFormation";
        Set<Property> platformProperties = new HashSet<>();
        /*String defaultKeyId = searchInCredentials();
        String defaultKeySecret = searchInCredentials();
        String defaultRegion = searchInConfig();*/
        platformProperties.add(new Property(
            AWS_REGION_KEY,
            PropertyType.TEXT,
            "The AWS Region this should be transformed to. (The imageId of possible EC2 machines depend on this)",
            true,
            AWS_REGION_DEFAULT
        ));/*
        platformProperties.add(new Property(
            AWS_KEY_ID_KEY,
            PropertyType.TEXT,
            "The Access key id",
            true,
            defaultKeyId
        ));
        platformProperties.add(new Property(
            AWS_KEY_SECRET_KEY,
            PropertyType.SECRET,
            "The Access key secret",
            true,
            defaultKeySecret
        ));*/
        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    protected CloudFormationLifecycle getInstance(TransformationContext context) throws Exception {
        return new CloudFormationLifecycle(context);
    }

}
