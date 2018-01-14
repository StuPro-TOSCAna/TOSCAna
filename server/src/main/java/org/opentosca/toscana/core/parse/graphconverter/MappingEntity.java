package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.util.ToscaKey;

import org.yaml.snakeyaml.nodes.MappingNode;

public class MappingEntity extends BaseEntity<Map<String, String>> {

    public MappingEntity(MappingNode node, EntityId id, ServiceGraph graph) {
        // TODO what to do with node?
        super(id, graph);
    }

    public MappingEntity(EntityId id, ServiceGraph graph) {
        super(id, graph);
    }

    @Override
    public String toString() {
        return (String.format("MappingEntity (id=%s", getId()));
    }

    public <V> V get(ToscaKey<V> key) {
        Optional<BaseEntity<?>> entity = graph.getChild(this, key);
        if (entity.isPresent()) {

            if (entity.get() instanceof ScalarEntity) {
                ScalarEntity scalarEntity = (ScalarEntity) entity.get();
                return TypeConverter.convert(scalarEntity.get(), key.getType());
            }
            return (V) entity.get();
        } else {
            return null;
        }
    }

    /**
     Returns the child entity specified by given key. If the child entity does not exist yet, creates the entity.
     */
    public MappingEntity getOrNewChild(ToscaKey<? extends BaseToscaElement> key) {
        MappingEntity child;
        Optional<BaseEntity<?>> entity = getChild(key);
        if (entity.isPresent()) {
            child = (MappingEntity) entity.get();
        } else {
            child = new MappingEntity(getId().descend(key), graph);
            graph.addEntity(child);
        }
        return child;
    }

}
