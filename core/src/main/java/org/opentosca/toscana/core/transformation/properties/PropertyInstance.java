package org.opentosca.toscana.core.transformation.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the instance of properties. That means that this is storing the values that get assigned to the
 * defined properties
 */
public class PropertyInstance {
    private Map<String, String> propertyValues;
    private Set<Property> properties;

    /**
     * Creates a new property instance with no set property values for the given list (set) of properties.
     *
     * @param properties the set of properties to create a property instance for. Is not allowed to be null, if no props
     *                   are needed add a empty set
     */
    public PropertyInstance(Set<Property> properties) {
        this.propertyValues = new HashMap<>();
        this.properties = properties;
    }

    /**
     * Sets the value of the property with its given key.
     * Throws a IllegalArgumentException if a property with the given key cannot be found
     * or if the entered value is invalid
     */
    public void setPropertyValue(Property property, String value) {
        this.setPropertyValue(property.getKey(), value);
    }

    /**
     * Checks if all Properties for the given Requirement type.
     *
     * @return true if all properties have been set and are valid, false otherwise
     */
    public boolean allPropertiesSetForType(RequirementType type) {
        return checkPropsSet(type, true);
    }

    private boolean isEqualRequirementType(RequirementType type, Property property) {
        return property.getRequirementType() == type;
    }

    private boolean isPropertySet(Map<String, String> propInstance, Property property) {
        return propInstance.get(property.getKey()) == null;
    }

    /**
     * Checks if all required properties are set and valid
     *
     * @param type the requirement type to for which to check the properties
     * @return true if all required properties are set and valid
     */
    public boolean allRequiredPropertiesSetForType(RequirementType type) {
        return checkPropsSet(type, false);
    }

    /**
     * Checks if properties are set for a specific requirement type
     *
     * @param type     the requirement type to check for
     * @param allProps if this is false it will check if all required properties are set,
     *                 otherwise all properties have to be set
     * @return true if all Properties in the wanted scope are set and valid
     */
    private boolean checkPropsSet(RequirementType type, boolean allProps) {
        Map<String, String> propInstance = getPropertyValues();
        for (Property property : properties) {
            if ((property.isRequired() || allProps) && isPropertySet(propInstance, property) &&
                isEqualRequirementType(type, property)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the value of the property with its given key.
     * Throws a IllegalArgumentException if a property with the given key cannot be found
     * or if the entered value is invalid
     */
    public void setPropertyValue(String key, String value) {
        for (Property p : properties) {
            if (p.getKey().equals(key)) {
                if (p.getType().validate(value)) {
                    this.propertyValues.put(key, value);
                    return;
                } else {
                    throw new IllegalArgumentException("The Property value is invalid!");
                }
            }
        }
        throw new IllegalArgumentException("A property with the given key does not exist! Key: " + key);
    }

    public Map<String, String> getPropertyValues() {
        return Collections.unmodifiableMap(propertyValues);
    }

    public Set<Property> getPropertySchema() {
        return Collections.unmodifiableSet(properties);
    }
}
