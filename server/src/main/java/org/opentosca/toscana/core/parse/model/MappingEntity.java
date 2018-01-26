package org.opentosca.toscana.core.parse.model;

import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.TypeConverter;
import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

public class MappingEntity extends Entity implements CollectionEntity {

    public MappingEntity(EntityId id, ServiceGraph graph) {
        super(id, graph);
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id='%s')", getId()));
    }

    public <V> V getValue(ToscaKey<V> key) {
        Optional<Entity> entity = getChild(key);
        if (entity.isPresent()) {
            try {
                return TypeConverter.convert(entity.get(), key, entity.get().getParent().get());
            } catch (AttributeNotSetException e) {
                graph.getLogger().warn("Accessing an unset attribute", e);
            }
        }
        return null;
    }

    /**
     Returns the child entity specified by given key. If the child entity does not exist yet, creates the entity.
     */
    public MappingEntity getOrNewChild(ToscaKey<?> key) {
        MappingEntity child;
        Optional<Entity> entity = getChild(key);
        if (entity.isPresent()) {
            child = (MappingEntity) entity.get();
        } else {
            EntityId childId = getId();
            if (key.getPredecessor().isPresent()) {
                if (key.getPredecessor().get().isList()) {
//                    childId = childId.descend();
                    // todo

                }
            }
            child = new MappingEntity(childId.descend(key), graph);
            graph.addEntity(child);
        }
        return child;
    }

    @Override
    public void addChild(Entity entity) {
        // todo
    }
}
