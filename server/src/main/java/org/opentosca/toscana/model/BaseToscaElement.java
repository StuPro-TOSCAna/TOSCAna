package org.opentosca.toscana.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.parse.converter.TypeWrapper;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Provides means to access the data of the attached {@code MappingEntity} in a type-safe but generic way.
 */
@EqualsAndHashCode
@ToString
public abstract class BaseToscaElement {

    public static final ToscaKey<String> TYPE = new ToscaKey<>("type");
    public static ToscaKey<Set<?>> PROPERTIES = new ToscaKey<>("properties")
        .type(Object.class);
    public static ToscaKey<Set<?>> ATTRIBUTES = new ToscaKey<>("attributes")
        .type(Object.class);

    private final MappingEntity mappingEntity;
    private final String name;

    protected BaseToscaElement(MappingEntity mappingEntity) {
        this.mappingEntity = mappingEntity;
        this.name = mappingEntity.getName();
    }

    /**
     Gets the value related with given key.
     */
    public <T> T get(ToscaKey<T> key) {
        return mappingEntity.getValue(key);
    }

    public <T> Collection<T> getCollection(ToscaKey<T> key) {
        return mappingEntity.getCollection(key);
    }

    protected <T> Set<T> getThisAsSet(Class<T> type) {
        TypeWrapper factory = new TypeWrapper();
        Collection<Entity> values = mappingEntity.getChildren();
        Set<T> results = new HashSet<>();
        for (Entity v : values) {
            T result = factory.wrapEntity((MappingEntity) v, type);
            results.add(result);
        }
        return results;
    }

    /**
     Sets the value related with given key to given value.
     */
    public <T> void set(ToscaKey<T> key, T value) {
        mappingEntity.setValue(key, value);
    }

    /**
     If value references by given name is null, sets this value to given value.
     Else, does nothing.
     */
    public <T> void setDefault(ToscaKey<T> key, T value) {
        if (mappingEntity.getValue(key) == null) {
            mappingEntity.setValue(key, value);
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
