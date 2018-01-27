package org.opentosca.toscana.core.transformation.platform;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.properties.PlatformProperty;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;

public class Platform {

    public static final String DEPLOY_AFTER_TRANSFORMATION_KEY = "deploy_after_transformation";
    public final String id;
    public final String name;
    public final Set<PlatformProperty> properties;
    public final boolean supportsDeployment;

    /**
     Creates a new platform.

     @param id                 short identifier of platform. must match regex [a-z_-]+
     @param name               displayable name of platform. must not be an empty string
     @param supportsDeployment set this to true if the plugin supports deployment within the TOSCAna Transformer
     @param properties         the properties the platform requires.
     */
    public Platform(String id, String name, boolean supportsDeployment, Set<PlatformProperty> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        this.supportsDeployment = supportsDeployment;
        if (id == null || !id.matches("[a-z_-]+") || name == null || name.isEmpty() ||
            properties == null) {
            throw new IllegalArgumentException(String.format("Platform '%s' is invalid", this));
        }

        // Add Deploy After transformation property if the platform supports deployment
        if (supportsDeployment) {
            this.properties.add(new PlatformProperty(
                DEPLOY_AFTER_TRANSFORMATION_KEY,
                PropertyType.BOOLEAN,
                "Should the Application be deployed after the Transformation?",
                true,
                "false"
            ));
        }
    }

    /**
     Creates a new platform. Without deployment Support

     @param id         short identifier of platform. must match regex [a-z_-]+
     @param name       displayable name of platform. must not be an empty string
     @param properties the properties the platform requires.
     */
    public Platform(String id, String name, Set<PlatformProperty> properties) {
        this(id, name, false, properties);
    }

    /**
     Creates a new platform. Used in case the platform does not require properties and does not Support in-app Deployments.

     @param id   short identifier of platform
     @param name displayable name of platform
     @see #Platform(String, String, Set)
     */
    public Platform(String id, String name) {
        this(id, name, false, new HashSet<>());
    }

    /**
     @return a list of properties which are necessary for a transformation to this platform.
     */
    public Set<Property> getProperties() {
        Set<Property> props = new HashSet<>();
        this.properties.forEach(e -> props.add(e.copy()));
        return Collections.unmodifiableSet(props);
    }

    @Override
    public String toString() {
        return String.format("Platform [id='%s', name='%s', properties='%s']", id, name, properties);
    }
}
