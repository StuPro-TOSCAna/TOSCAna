package org.opentosca.toscana.core.parse.graphconverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.model.EntityId;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceEntity extends BaseEntity<List<String>> {

    private final SequenceNode sequenceNode;

    public SequenceEntity(SequenceNode node, EntityId id, ServiceGraph graph) {
        super(id, graph);
        this.sequenceNode = node;
    }

    @Override
    public List<String> get() {
        List<String> values = new ArrayList<>();
        for (Node node : sequenceNode.getValue()) {
            String value = ((ScalarNode) node).getValue();
            values.add(value);
        }
        return values;
    }

    @Override
    public Set<BaseEntity<?>> getChildren() {
        Set<BaseEntity<?>> children = new HashSet<>();
        Set<BaseEntity<?>> entities = graph.getChildren(this);
        for (BaseEntity entity : entities) {
            Iterator<BaseEntity> it = entity.getChildren().iterator();
            BaseEntity child = it.next();
            children.add(child);
        }
        return children;
    }

    @Override
    public String toString() {
        return (String.format("SequenceEntity (id=%s", getId()));
    }
}


