package org.opentosca.toscana.core.transformation.platform;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.properties.Property;

public class Platform {

    public final String id;
    public final String name;
    public final Set<Property> properties;

    /**
     Creates a new platform.

     @param id         short identifier of platform. must match regex [a-z_-]+
     @param name       displayable name of platform. must not be an empty string
     @param properties the properties the platform requires.
     */
    public Platform(String id, String name, Set<Property> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
        if (id == null || !id.matches("[a-z_-]+") || name == null || name.isEmpty() ||
            properties == null) {
            throw new IllegalArgumentException(String.format("Platform '%s' is invalid", this));
        }
    }

    /**
     Creates a new platform. Used in case the platform does not require properties.

     @param id   short identifier of platform
     @param name displayable name of platform
     @see #Platform(String, String, Set)
     */
    public Platform(String id, String name) {
        this(id, name, new HashSet<>());
    }

    /**
     @return a list of properties which are necessary for a transformation to this platform.
     */
    public Set<Property> getProperties() {
        return Collections.unmodifiableSet(properties);
    }

    public String toString() {
        return String.format("Platform [id='%s', name='%s', properties='%s']", id, name, properties);
    }
}
