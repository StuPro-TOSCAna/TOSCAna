package org.opentosca.toscana.core.parse.graphconverter;

import org.opentosca.toscana.model.EntityId;

public class ScalarEntity extends BaseEntity {

    private String value;

    public ScalarEntity(String value, EntityId id, ServiceGraph graph) {
        super(id, graph);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ScalarEntity (id='%s', value='%s')", getId(), getValue());
    }

    public String getValue() {
        return value;
    }

    public ScalarEntity setValue(String value) {
        this.value = value;
        return this;
    }
}
