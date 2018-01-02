package org.opentosca.toscana.core.parse.converter.visitor;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;

public class SimpleContext extends AbstractParameter<SimpleContext> {

    private final String name;

    public SimpleContext(String name) {
        this.name = name;
    }

    @Override
    public SimpleContext copy() {
        return this;
    }

    @Override
    public SimpleContext self() {
        return this;
    }

    public String getName() {
        return name;
    }
}
