package org.opentosca.toscana.core.transformation.properties;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;

/**
 Represents the instance of properties.
 That means that this is storing the values that get assigned to the defined properties.
 */
public class PropertyInstance {
    private final Map<String, Property> properties;
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
        this.properties = properties.stream().collect(Collectors.toMap(Property::getKey, p -> p));

        if (!properties.stream().allMatch(Property::isValid)) {
            transformation.setState(TransformationState.INPUT_REQUIRED);
        }
    }

    public Map<String, Property> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /**
     @param key the key of a corresponding property
     @return the value of the property matching given key
     @throws NoSuchPropertyException if no property with given key exists
     */
    public Optional<String> get(String key) throws NoSuchPropertyException {
        Property p = properties.get(key);
        if (p != null) {
            return p.getValue();
        } else {
            throw new NoSuchPropertyException(key);
        }
    }

    /**
     @return the value of the corresponding property
     @throws NoSuchElementException if no property with given key exists or
     if no value or default value for corresponding property is set
     */
    public String getOrThrow(String key) {
        Optional<String> value = null;
        try {
            value = get(key);
            return value.orElseThrow(() -> new NoSuchElementException(
                String.format("Property with key '%s' does neither have a value nor a default value," +
                    "but is required to have one.", key)));
        } catch (NoSuchPropertyException e) {
            throw new NoSuchElementException(String.format("Property with key '%s' does not exist", e.getKey()));
        }
    }

    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     Checks if all required properties are set and valid

     @return true if all required properties are set and valid
     */
    public boolean isValid() {
        return properties.values().stream().allMatch(Property::isValid);
    }

    /**
     Sets the value of the property with its given key.

     @return true if corresponding property is now valid, false otherwise
     */
    public boolean set(String key, String value) throws NoSuchPropertyException {
        Property property = properties.get(key);
        if (property != null) {
            property.setValue(value);
        } else {
            throw new NoSuchPropertyException(key);
        }
        changeTransformationState();
        return property.isValid();
    }

    private void changeTransformationState() {
        boolean allValid = isValid();
        if (allValid && transformation.getState() == TransformationState.INPUT_REQUIRED) {
            transformation.setState(TransformationState.READY);
        } else if (!allValid && transformation.getState() == TransformationState.READY) {
            transformation.setState(TransformationState.INPUT_REQUIRED);
        }
    }
}
