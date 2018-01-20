package org.opentosca.toscana.core.parse.model;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

public abstract class Entity implements Comparable<Entity> {

    protected final ServiceGraph graph;
    private final EntityId id;

    public Entity(EntityId id, ServiceGraph graph) {
        this.id = id;
        this.graph = graph;
    }

    public <V> void setValue(ToscaKey<V> key, V value) {
        EntityId newId = id.descend(key);
        Entity entity;
        if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            entity = ((BaseToscaElement) value).getBackingEntity();
        } else {
            entity = new ScalarEntity(value.toString(), newId, graph);
        }
        graph.addEntity(entity);
    }

    public String getName() {
        return id.getName();
    }

    public Optional<Entity> getChild(String key) {
        return graph.getChild(this, key);
    }

    public Optional<Entity> getChild(ToscaKey key) {
        return graph.getChild(this, key);
    }

    public Entity getChildOrThrow(String key) {
        return graph.getChildOrThrow(this, key);
    }

    public Entity getChildOrThrow(ToscaKey key) {
        return graph.getChildOrThrow(this, key);
    }

    public EntityId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity that = (Entity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(Entity other) {
        return id.compareTo(other.id);
    }

    public Collection<Entity> getChildren() {
        return graph.getChildren(this);
    }

    public <T> Set<T> getCollection(ToscaKey<T> key) {
        return graph.getCollection(this, key);
    }

    public ServiceGraph getGraph() {
        return graph;
    }

    public Entity getParent() {
        return graph.getParent(this);
    }
}
