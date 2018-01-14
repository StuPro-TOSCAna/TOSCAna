package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

public abstract class BaseEntity<T> implements Comparable<BaseEntity<T>> {

    protected final ServiceGraph graph;
    private final EntityId id;
    private T value;

    public BaseEntity(EntityId id, ServiceGraph graph) {
        this.id = id;
        this.graph = graph;
    }

    /**
     Establishes a link between this instance's property (defined by sourceKey) and the targets targetKey.
     In other words, the linked property now behaves like a symbolic link.
     */
    public <E> void link(ToscaKey<E> sourceKey, BaseEntity target, ToscaKey<E> targetKey) {
        // TODO move link to graph.
//        content.link(sourceKey, target, targetKey);
    }

    public <V> void set(ToscaKey<V> key, V value) {
        EntityId newId = id.descend(key);
        BaseEntity entity;
        if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            entity = ((BaseToscaElement) value).getBackingEntity();
        } else {
            entity = new ScalarEntity(value.toString(), newId, graph);
        }
        graph.addEntity(entity);
        // TODO rewrite
        return;
    }

    public String getName() {
        return id.getName();
    }

    public T get() {
        return value;
    }

    public Optional<BaseEntity<?>> getChild(String key) {
        return graph.getChild(this, key);
    }

    public BaseEntity<T> set(T value) {
        this.value = value;
        return this;
    }

    public EntityId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(BaseEntity<T> tBaseEntity) {
        return id.compareTo(tBaseEntity.id);
    }

    public Set<BaseEntity<?>> getChildren() {
        return graph.getChildren(this);
    }
}
