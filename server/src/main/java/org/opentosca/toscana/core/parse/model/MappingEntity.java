package org.opentosca.toscana.core.parse.model;

import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.TypeConverter;
import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

import org.slf4j.Logger;

public class MappingEntity extends Entity {

    private final Logger logger;

    public MappingEntity(EntityId id, ServiceGraph graph) {
        super(id, graph);
        this.logger = graph.getLog().getLogger(getClass());
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id='%s')", getId()));
    }

    public <V> V getValue(ToscaKey<V> key) {
        Optional<Entity> entity = graph.getChild(this, key);
        if (entity.isPresent()) {
            try {
                return TypeConverter.convert(entity.get(), key);
            } catch (AttributeNotSetException e) {
                logger.warn("Accessing an unset attribute", e);
            }
        }
        return null;
    }

    /**
     Returns the child entity specified by given key. If the child entity does not exist yet, creates the entity.
     */
    public MappingEntity getOrNewChild(ToscaKey key) {
        MappingEntity child;
        Optional<Entity> entity = getChild(key);
        if (entity.isPresent()) {
            child = (MappingEntity) entity.get();
        } else {
            child = new MappingEntity(getId().descend(key), graph);
            graph.addEntity(child);
        }
        return child;
    }
}
