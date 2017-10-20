package org.opentosca.toscana.core.transformation.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyInstance {
	private Map<String, String> propertyValues;
	private Set<Property> properties;

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
		Map<String, String> propInstance = getPropertyValues();
		for (Property property : properties) {
			if (propInstance.get(property.getKey()) == null && property.getRequirementType() == type) {
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
