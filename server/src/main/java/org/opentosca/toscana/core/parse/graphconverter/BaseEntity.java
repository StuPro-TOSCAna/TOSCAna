package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Collection;
import java.util.Optional;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

public abstract class BaseEntity implements Comparable<BaseEntity> {

    protected final ServiceGraph graph;
    private final EntityId id;

    public BaseEntity(EntityId id, ServiceGraph graph) {
        this.id = id;
        this.graph = graph;
    }
    
    public <V> void setValue(ToscaKey<V> key, V value) {
        EntityId newId = id.descend(key);
        BaseEntity entity;
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

    public Optional<BaseEntity> getChild(String key) {
        return graph.getChild(this, key);
    }

    public Optional<BaseEntity> getChild(ToscaKey key) {
        return graph.getChild(this, key);
    }

    public BaseEntity getChildOrThrow(String key) {
        return graph.getChildOrThrow(this, key);
    }
    
    public BaseEntity getChildOrThrow(ToscaKey key) {
        return graph.getChildOrThrow(this, key);
    }


    public EntityId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(BaseEntity other) {
        return id.compareTo(other.id);
    }

    public Collection<BaseEntity> getChildren() {
        return graph.getChildren(this);
    }

    public ServiceGraph getGraph() {
        return graph;
    }

    public BaseEntity getParent() {
        return graph.getParent(this);
    }
}
