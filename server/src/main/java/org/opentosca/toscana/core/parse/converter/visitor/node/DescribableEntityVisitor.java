package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.AbstractEntityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.DescribableEntity.DescribableEntityBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;

public abstract class DescribableEntityVisitor<NodeT extends DescribableEntity, BuilderT extends DescribableEntityBuilder> extends AbstractEntityVisitor<NodeT, BuilderT> {

    @Override
    public ConversionResult<NodeT> visit(TNodeTemplate node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        builder.description(node.getDescription());
        super.visit(node, parameter);
        NodeT resultingNode = (NodeT) builder.build();
        ConversionResult<NodeT> result = new ConversionResult<NodeT>(resultingNode, parameter);
        return result;
    }

    @Override
    public ConversionResult<NodeT> visit(TCapabilityAssignment node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        String key = parameter.getKey();
        handleCapability(node, builder, key);
        return null;
    }

    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        // handle capability conversion in implementation
    }
}
