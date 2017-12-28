package org.opentosca.toscana.core.parse.converter.visitor;

import org.opentosca.toscana.model.AbstractEntity;
import org.opentosca.toscana.model.AbstractEntity.AbstractEntityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;

public abstract class AbstractEntityVisitor<NodeT extends AbstractEntity, BuilderT extends AbstractEntityBuilder> extends AbstractVisitor<ConversionResult<NodeT>, Context<BuilderT>> {

    @Override
    public ConversionResult<NodeT> visit(TPropertyAssignment node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        Object value = node.getValue();
        handleProperty(node, parameter, builder, value);
        return null;
    }

    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        // override in concrete implementation
    }
}
