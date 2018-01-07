package org.opentosca.toscana.plugins.cloudfoundry;

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
public class CloudFoundryPlugin extends LifecycleAwarePlugin<CloudFoundryLifecycle> {

    public final static String CF_PROPERTY_KEY_USERNAME = "username";
    public final static String CF_PROPERTY_KEY_PASSWORD = "password";
    public final static String CF_PROPERTY_KEY_API = "apiHost";
    public final static String CF_PROPERTY_KEY_SPACE = "space";
    public final static String CF_PROPERTY_KEY_ORGANIZATION = "organization";

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryPlugin.class);

    public CloudFoundryPlugin() {
        super(getPlatformDetails());
    }

    @Override
    protected CloudFoundryLifecycle getInstance(TransformationContext context) throws Exception {
        return new CloudFoundryLifecycle(context);
    }

    private static Platform getPlatformDetails() {
        String platformId = "cloud-foundry";
        String platformName = "CloudFoundry";
        Set<Property> platformProperties = new HashSet<>();

        Property cfUserName = new Property(CF_PROPERTY_KEY_USERNAME, PropertyType.TEXT,
            "Username of CloudFoundry provideraccount",
            false);
        Property cfUserPw = new Property(CF_PROPERTY_KEY_PASSWORD, PropertyType.SECRET,
            "Password of CloudFoundry provideraccount",
            false);
        Property cfEndpoint = new Property(CF_PROPERTY_KEY_API, PropertyType.TEXT,
            "The endpoint of the provider",
            false);
        Property cfSpace = new Property(CF_PROPERTY_KEY_SPACE, PropertyType.TEXT,
            "The space of the useraccount which should be used to deploy",
            false);
        Property cfOrganization = new Property(CF_PROPERTY_KEY_ORGANIZATION, PropertyType.TEXT,
            "The space of the useraccount which should be used to deploy",
            false);

        platformProperties.add(cfUserName);
        platformProperties.add(cfUserPw);
        platformProperties.add(cfEndpoint);
        platformProperties.add(cfSpace);
        platformProperties.add(cfOrganization);

        return new Platform(platformId, platformName, platformProperties);
    }
}
