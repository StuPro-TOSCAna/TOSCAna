package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 (TOSCA Simple Profile in YAML Version 1.1, p. 222)
 */
@EqualsAndHashCode
@ToString
public class Apache extends WebServer {

    public Apache(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
