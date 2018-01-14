package org.opentosca.toscana.core.transformation.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;

/**
 Represents the instance of properties.
 That means that this is storing the values that get assigned to the defined properties.
 */
public class PropertyInstance {
    private final Map<String, String> propertyValues;
    private final Set<Property> properties;
    private final Transformation transformation;

    /**
     Creates a new property instance with no set property values for the given list (set) of properties.

     @param properties     the set of properties to create a property instance for.
     Is not allowed to be null, if no props are needed add a empty set
     @param transformation the related transformation. It's TransformationState will be altered by this instance
     according to the state of the properties
     */
    public PropertyInstance(Set<Property> properties, Transformation transformation) {
        this.transformation = transformation;
        this.propertyValues = new HashMap<>();
        this.properties = properties;
        properties.forEach(e -> {
            propertyValues.put(e.getKey(), e.getDefaultValue());
        });
        //Set state to input required if there are required properties
        if (properties.stream().anyMatch(Property::isRequired)) {
            transformation.setState(TransformationState.INPUT_REQUIRED);
        }
    }

    /**
     Sets the value of the property with its given key.

     @throws IllegalArgumentException if a property with the given key cannot be found or if the entered value is invalid
     */
    public void setPropertyValue(Property property, String value) {
        this.setPropertyValue(property.getKey(), value);
    }

    /**
     Checks if all Properties for the given Requirement type.

     @return true if all properties have been set and are valid, false otherwise
     */
    public boolean allPropertiesSet() {
        return checkPropsSet(true);
    }

    private boolean isPropertySet(Map<String, String> propInstance, Property property) {
        return propInstance.get(property.getKey()) == null;
    }

    /**
     Checks if all required properties are set and valid

     @return true if all required properties are set and valid
     */
    public boolean requiredPropertiesSet() {
        return checkPropsSet(false);
    }

    /**
     Checks if properties are set for a specific requirement type

     @param allProps if this is false it will check if all required properties are set,
     otherwise all properties have to be set
     @return true if all Properties in the wanted scope are set and valid
     */
    private boolean checkPropsSet(boolean allProps) {
        Map<String, String> propInstance = getPropertyValues();
        for (Property property : properties) {
            if ((property.isRequired() || allProps) && isPropertySet(propInstance, property)) {
                return false;
            }
        }
        return true;
    }

    /**
     Sets the value of the property with its given key.

     @throws IllegalArgumentException if a property with given key cannot be found or if the entered value is invalid
     */
    public void setPropertyValue(String key, String value) {
        setPropertyInternal(key, value);
        if (requiredPropertiesSet() && transformation.getState() == TransformationState.INPUT_REQUIRED) {
            transformation.setState(TransformationState.READY);
        }
    }

    private void setPropertyInternal(String key, String value) {
        for (Property p : properties) {
            if (p.getKey().equals(key)) {
                if (p.getType().validate(value)) {
                    this.propertyValues.put(key, value);
                    return;
                } else {
                    throw new IllegalArgumentException("The Property value is invalid");
                }
            }
        }
        throw new IllegalArgumentException("A property with the given key does not exist. Key: " + key);
    }

    public Map<String, String> getPropertyValues() {
        return Collections.unmodifiableMap(propertyValues);
    }

    public Optional<String> getPropertyValue(String key) {
        return Optional.ofNullable(propertyValues.get(key));
    }

    public Set<Property> getPropertySchema() {
        return Collections.unmodifiableSet(properties);
    }
}
