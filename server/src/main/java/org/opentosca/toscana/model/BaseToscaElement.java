package org.opentosca.toscana.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.graphconverter.BaseEntity;
import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.ToscaFactory;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public abstract class BaseToscaElement {

    // TODO what kind of class is Property and Attribute?
    public static ToscaKey<Set<Object>> PROPERTIES = new ToscaKey<>("properties")
        .type(Object.class);
    public static ToscaKey<Set<Object>> ATTRIBUTES = new ToscaKey<>("attributes")
        .type(Object.class);

    private final MappingEntity mappingEntity;
    private final String name;

    protected BaseToscaElement(MappingEntity mappingEntity) {
        this.mappingEntity = mappingEntity;
        this.name = mappingEntity.getName();
    }

    public <T> T get(ToscaKey<T> key) {
        T value = mappingEntity.get(key);
        if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            value = (T) new ToscaFactory().wrapEntity((MappingEntity) value, key.getType());
        }
        return value;
    }

    public <T> Set<T> getCollection(ToscaKey<T> key) {
        Set<T> values = new HashSet<>();
        ToscaFactory factory = new ToscaFactory();
        Optional<BaseEntity<?>> entity = mappingEntity.getChild(key.name);
        if (entity.isPresent()) {
            for (BaseEntity child : entity.get().getChildren()) {
                T value = factory.wrapEntity((MappingEntity) child, key.getType());
                values.add(value);
            }
        }
        return values;
    }

    protected <T> Set<T> getThisAsSet(Class<T> type) {
        ToscaFactory factory = new ToscaFactory();
        Set<BaseEntity<?>> values = mappingEntity.getChildren();
        Set<T> results = new HashSet<>();
        for (BaseEntity v : values) {
            T result = factory.wrapEntity((MappingEntity) v, type);
            results.add(result);
        }
        return results;
    }

    public <T> void set(ToscaKey<T> key, T value) {
        mappingEntity.set(key, value);
    }

    /**
     If value references by given name is null, sets this value to given value.
     Else, does nothing.
     */
    public <T> void setDefault(ToscaKey<T> key, T value) {
        if (mappingEntity.get(key) == null) {
            mappingEntity.set(key, value);
        }
    }

    public String getEntityName() {
        return name;
    }

    /**
     Returns the underlying raw entity.
     Danger: Do not use this outside of the converter.
     Your fellow developers will find you and hunt you down.
     */
    public MappingEntity getBackingEntity() {
        return mappingEntity;
    }
    
    protected MappingEntity getChildEntity(ToscaKey<? extends BaseToscaElement> childKey) {
        return mappingEntity.getOrNewChild(childKey);
    }
}
