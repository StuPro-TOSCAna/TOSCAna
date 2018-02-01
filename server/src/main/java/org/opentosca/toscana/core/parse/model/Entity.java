package org.opentosca.toscana.core.parse.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.ToscaTemplateException;
import org.opentosca.toscana.core.parse.converter.TypeConverter;
import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

import org.slf4j.Logger;

public abstract class Entity implements Comparable<Entity> {

    protected final ServiceGraph graph;
    private final EntityId id;
    private final Logger logger;

    public Entity(EntityId id, ServiceGraph graph) {
        this.id = id;
        this.graph = graph;
        this.logger = graph.getLogger();
    }

    public <V> void setValue(ToscaKey<V> key, V value) {
        EntityId id = this.id.descend(key);
        final Entity newEntity;
        if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            newEntity = ((BaseToscaElement) value).getBackingEntity();
        } else {
            newEntity = new ScalarEntity(value.toString(), id, graph);
        }
        Optional<Entity> oldEntity = graph.getEntity(id);
        if (oldEntity.isPresent()) {
            graph.replaceEntity(oldEntity.get(), newEntity);
        } else {
            graph.addEntity(newEntity);
        }
    }

    public String getName() {
        Optional<Entity> parent = getParent();
        if (parent.isPresent()) {
            Connection incomingConnection = graph.getEdge(parent.get(), this);
            if (incomingConnection != null) {
                return incomingConnection.getKey();
            }
        }
        return this.id.getName();
    }

    public Optional<Entity> getChild(ToscaKey<?> key) {
        Entity source = this;
        if (key.getPredecessor().isPresent()) {
            Optional<Entity> intermediateEntity = getChild(key.getPredecessor().get());
            if (intermediateEntity.isPresent()) {
                source = intermediateEntity.get();
            } else {
                return Optional.empty();
            }
        }
        return source.getChild(key.name);
    }

    /**
     Returns the associated child for given name and source entity.

     @return null if no child associated with given name was found
     */
    public Optional<Entity> getChild(String key) {
        for (Connection connection : graph.outgoingEdgesOf(this)) {
            if (connection.getKey().equals(key)) {
                return Optional.of(connection.getTarget());
            }
        }
        return Optional.empty();
    }

    public Entity getChildOrThrow(String key) {
        Optional<Entity> optionalEntity = getChild(key);
        return optionalEntity.orElseThrow(() -> new ToscaTemplateException(
            String.format("Entity '%s' is referenced but does not exist", getId().descend(key))
        ));
    }

    public Entity getChildOrThrow(ToscaKey key) {
        Optional<Entity> optionalEntity = getChild(key);
        return optionalEntity.orElseThrow(() -> new ToscaTemplateException(
            String.format("Entity '%s' is referenced but does not exist", getId().descend(key))
        ));
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

    public <T> Collection<T> getCollection(ToscaKey<T> key) {
        Set<T> values = new HashSet<>();
        Optional<Entity> child = getChild(key);
        if (child.isPresent()) {
            for (Entity grandChild : child.get().getChildren()) {
                try {
                    T value = TypeConverter.convert(grandChild, key, child.get());
                    values.add(value);
                } catch (AttributeNotSetException e) {
                    logger.warn("Trying to access an unset attribute - skipping.", e);
                }
            }
        }
        return values;
    }

    public ServiceGraph getGraph() {
        return graph;
    }

    public Collection<Entity> getChildren() {
        Set<Entity> children = new HashSet<>();
        for (Connection connection : graph.outgoingEdgesOf(this)) {
            children.add(connection.getTarget());
        }
        return children;
    }

    public Optional<Entity> getParent() {
        EntityId parentId = getId().ascend();
        if (parentId == null) {
            return Optional.empty();
        }
        Entity parent = graph.getEntityOrThrow(parentId);
        return Optional.of(parent);
    }

    @Override
    public int compareTo(Entity other) {
        return id.compareTo(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
