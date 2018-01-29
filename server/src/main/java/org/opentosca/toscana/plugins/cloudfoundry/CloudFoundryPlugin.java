package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.TOSCAnaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformProperty;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFoundryPlugin extends TOSCAnaPlugin<CloudFoundryLifecycle> {

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
        Set<PlatformProperty> platformProperties = new HashSet<>();

        PlatformProperty cfUserName = new PlatformProperty(CF_PROPERTY_KEY_USERNAME, PropertyType.TEXT,
            "Username of CloudFoundry provideraccount",
            false);
        PlatformProperty cfUserPw = new PlatformProperty(CF_PROPERTY_KEY_PASSWORD, PropertyType.SECRET,
            "Password of CloudFoundry provideraccount",
            false);
        PlatformProperty cfEndpoint = new PlatformProperty(CF_PROPERTY_KEY_API, PropertyType.TEXT,
            "The endpoint of the provider",
            false);
        PlatformProperty cfSpace = new PlatformProperty(CF_PROPERTY_KEY_SPACE, PropertyType.TEXT,
            "The space of the useraccount which should be used to deploy",
            false);
        PlatformProperty cfOrganization = new PlatformProperty(CF_PROPERTY_KEY_ORGANIZATION, PropertyType.TEXT,
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
