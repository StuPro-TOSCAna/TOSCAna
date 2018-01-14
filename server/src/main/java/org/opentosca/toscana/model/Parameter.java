package org.opentosca.toscana.model;

import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.util.ParameterConverter;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents an input or output parameter
 */
@EqualsAndHashCode
@ToString
public class Parameter extends DescribableEntity implements Property {

    public static final ToscaKey<String> TYPE = new ToscaKey<>("type");

    public static final ToscaKey<String> VALUE = new ToscaKey<>("value");

    public static final ToscaKey<Boolean> REQUIRED = new ToscaKey<>("required")
        .type(Boolean.class);

    public static final ToscaKey<String> DEFAULT = new ToscaKey<>("default");

    public Parameter(MappingEntity mappingEntity) {
        super(mappingEntity);
        setDefault(REQUIRED, true);
    }

    @Override
    public String getKey() {
        return getEntityName();
    }

    /**
     @return {@link #REQUIRED}
     */
    @Override
    public boolean isRequired() {
        return get(REQUIRED);
    }

    /**
     @return {@link #TYPE}
     */
    @Override
    public PropertyType getType() {
        String type = get(TYPE);
        return ParameterConverter.convertType(type);
    }

    /**
     @return {@link #VALUE}
     */
    @Override
    public Optional<String> getValue() {
        return Optional.ofNullable(get(VALUE));
    }

    @Override
    public void setValue(String value) {
        set(VALUE, value);
    }

    /**
     @return {@link #DEFAULT}
     */
    @Override
    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(get(DEFAULT));
    }
}
