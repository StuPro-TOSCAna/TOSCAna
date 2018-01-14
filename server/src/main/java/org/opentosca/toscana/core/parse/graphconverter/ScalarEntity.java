package org.opentosca.toscana.core.parse.graphconverter;

import org.opentosca.toscana.model.EntityId;

public class ScalarEntity extends BaseEntity<String> {
    public ScalarEntity(String value, EntityId id, ServiceGraph graph) {
        super(id, graph);
        set(value);
    }

    @Override
    public String toString() {
        return String.format("ScalarEntity (id='%s', value='%s')", getId(), get());
    }
}
