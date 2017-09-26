package org.opentosca.toscana.core.data;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Platform {

    public final String id;
    public final String name;
    public final Set<Property> requiredProperties;

    /**
     * Creates a new platform.
     *
     * @param id                 short identifier of platform. must match regex [a-z_-]+
     * @param name               displayable name of platform. must not be an empty string
     * @param requiredProperties the properties the platform requires.
     */
    public Platform(String id, String name, Set<Property> requiredProperties) {
        this.id = id;
        this.name = name;
        this.requiredProperties = requiredProperties;
        if (id == null || !id.matches("[a-z_-]+") || name == null || name.isEmpty() ||
                requiredProperties == null) {
            throw new IllegalArgumentException(String.format("Platform '%s' is invalid", this));
        }
    }

    /**
     * Creates a new platform. Used in case the platform does not require properties.
     *
     * @param id   short identifier of platform
     * @param name displayable name of platform
     * @see #Platform(String, String, Set<Property>)
     */
    Platform(String id, String name) {
        this(id, name, new HashSet<Property>());
    }


    /**
     * @return a list of properties which are necessary for a transformation to this platform.
     */
    public List<Property> getRequiredProperties() {
        // TODO
        // Remember to return a deep copy of the properties..!
        throw new UnsupportedOperationException();

    }

    public String toString() {
        return String.format("Platform [id='%s', name='%s', requiredProperties='%s']", id, name, requiredProperties);
    }
}
