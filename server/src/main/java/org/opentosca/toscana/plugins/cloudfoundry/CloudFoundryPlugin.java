package org.opentosca.toscana.plugins.cloudfoundry;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudFoundryPlugin extends ToscanaPlugin<CloudFoundryLifecycle> {

    public final static String CF_PROPERTY_KEY_USERNAME = "CF_Instance_Username";
    public final static String CF_PROPERTY_KEY_PASSWORD = "CF_Instance_Password";
    public final static String CF_PROPERTY_KEY_API = "CF_Instance_Api";
    public final static String CF_PROPERTY_KEY_SPACE = "CF_Instance_Space";
    public final static String CF_PROPERTY_KEY_ORGANIZATION = "CF_Instance_Organization";

    private final static Logger logger = LoggerFactory.getLogger(CloudFoundryPlugin.class);

    public CloudFoundryPlugin() {
        super(getPlatformDetails());
    }

    private static Platform getPlatformDetails() {
        String platformId = "cloud-foundry";
        String platformName = "CloudFoundry";
        Set<PlatformInput> platformProperties = new HashSet<>();

        PlatformInput cfUserName = new PlatformInput(CF_PROPERTY_KEY_USERNAME, PropertyType.TEXT,
            "Username of CloudFoundry provideraccount",
            true);
        PlatformInput cfUserPw = new PlatformInput(CF_PROPERTY_KEY_PASSWORD, PropertyType.SECRET,
            "Password of CloudFoundry provideraccount",
            true);
        PlatformInput cfEndpoint = new PlatformInput(CF_PROPERTY_KEY_API, PropertyType.TEXT,
            "The endpoint of the provider",
            true);
        PlatformInput cfSpace = new PlatformInput(CF_PROPERTY_KEY_SPACE, PropertyType.TEXT,
            "The space of the useraccount which should be used to deploy",
            true);
        PlatformInput cfOrganization = new PlatformInput(CF_PROPERTY_KEY_ORGANIZATION, PropertyType.TEXT,
            "The organization of the useraccount which should be used to deploy",
            true);

        platformProperties.add(cfUserName);
        platformProperties.add(cfUserPw);
        platformProperties.add(cfEndpoint);
        platformProperties.add(cfSpace);
        platformProperties.add(cfOrganization);

        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    public CloudFoundryLifecycle getInstance(TransformationContext context) throws Exception {
        return new CloudFoundryLifecycle(context);
    }
}
