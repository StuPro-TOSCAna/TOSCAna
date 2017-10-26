package org.opentosca.toscana.core.transformation.properties;

/**
 * This class describes the "Model" of a property, this means it defines the expected key name and the type the value
 * has to be of.
 * <p>
 * The values get stored in the @PropertyInstance class
 */
public class Property {
    private String key;
    private PropertyType type;
    private RequirementType requirementType;

    private String description;
    private boolean required;

    /**
     * Constructs a new property object, describing the key of a property and the type of the value
     *
     * @param key             the unique key of the property
     * @param type            the expected "datatype" of the property value
     * @param requirementType classifies the property for a specific type of process (Transformation or deployment)
     * @param description     a short description of the property (should not exceed 200 characters, does not get
     *                        checked tough)
     * @param required        determines if the property is required or not
     */
    public Property(String key, PropertyType type, RequirementType requirementType,
                    String description, boolean required) {
        this.key = key;
        this.type = type;
        this.requirementType = requirementType;
        this.description = description;
        this.required = required;
    }

    /**
     * This constructor creates a Property with no description and the created property is required
     *
     * @param key             the unique key of the property
     * @param type            the expected "datatype" of the property value
     * @param requirementType classifies the property for a specific type of process (Transformation or deployment)
     */
    public Property(String key, PropertyType type, RequirementType requirementType) {
        this(key, type, requirementType, "", true);
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

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }
}
