package org.opentosca.toscana.core.transformation.properties;

public class Property {
	private String key;
	private PropertyType type;
	private RequirementType requirementType;

	public Property(String key, PropertyType type, RequirementType requirementType) {
		this.key = key;
		this.type = type;
		this.requirementType = requirementType;
	}

	public String getKey() {
		return key;
	}

	public PropertyType getType() {
		return type;
	}

	public RequirementType getRequirementType() {
		return requirementType;
	}
}
