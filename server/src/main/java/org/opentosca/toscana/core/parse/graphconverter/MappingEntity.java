package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Optional;

import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

public class MappingEntity extends BaseEntity {

    public MappingEntity(EntityId id, ServiceGraph graph) {
        super(id, graph);
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id='%s')", getId()));
    }

    public <V> V getValue(ToscaKey<V> key) {
        Optional<BaseEntity> entity = graph.getChild(this, key);
        if (entity.isPresent()) {
            return new TypeConverter().convert(entity.get(), key);
        } else {
            return null;
        }
    }

    /**
     Returns the child entity specified by given key. If the child entity does not exist yet, creates the entity.
     */
    public MappingEntity getOrNewChild(ToscaKey key) {
        MappingEntity child;
        Optional<BaseEntity> entity = getChild(key);
        if (entity.isPresent()) {
            child = (MappingEntity) entity.get();
        } else {
            child = new MappingEntity(getId().descend(key), graph);
            graph.addEntity(child);
        }
        return child;
    }
}
