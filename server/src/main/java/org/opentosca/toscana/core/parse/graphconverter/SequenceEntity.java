package org.opentosca.toscana.core.parse.graphconverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.model.EntityId;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceEntity extends BaseEntity {

    private final SequenceNode sequenceNode;

    public SequenceEntity(SequenceNode node, EntityId id, ServiceGraph graph) {
        super(id, graph);
        this.sequenceNode = node;
    }

    public List<String> get() {
        List<String> values = new ArrayList<>();
        for (Node node : sequenceNode.getValue()) {
            String value = ((ScalarNode) node).getValue();
            values.add(value);
        }
        return values;
    }

    @Override
    public List<BaseEntity> getChildren() {
        List<BaseEntity> children = new ArrayList<>();
        Set<BaseEntity> entities = graph.getChildren(this);
        List<BaseEntity> orderedEntities = new LinkedList<>(entities);
        orderedEntities.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
        for (BaseEntity entity : orderedEntities) {
            if (entity instanceof ScalarEntity) {
                children.add(entity);
            } else {
                Iterator<BaseEntity> it = entity.getChildren().iterator();
                BaseEntity child = it.next();
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public String toString() {
        return (String.format("SequenceEntity (id=%s", getId()));
    }
}


