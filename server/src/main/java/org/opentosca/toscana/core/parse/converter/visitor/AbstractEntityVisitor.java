package org.opentosca.toscana.core.parse.converter.visitor;

import java.util.Map;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunctionTemplate;
import org.opentosca.toscana.core.parse.converter.util.ParameterConverter;
import org.opentosca.toscana.model.AbstractEntity;
import org.opentosca.toscana.model.AbstractEntity.AbstractEntityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;

public abstract class AbstractEntityVisitor<NodeT extends AbstractEntity, BuilderT extends AbstractEntityBuilder> extends AbstractVisitor<ConversionResult<NodeT>, Context<BuilderT>> {

    @Override
    public ConversionResult<NodeT> visit(TPropertyAssignment node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        Object value = node.getValue();
        if (value instanceof Map) {
            handleFunction(node, parameter, builder);
        } else {
            handleProperty(node, parameter, builder, value);
        }
        return null;
    }

    private void handleFunction(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder) {
        ToscaFunctionTemplate function = new ParameterConverter().convert(parameter.getKey(), node, parameter);
        parameter.getFunctions().add(function);
        handleProperty(node, parameter, builder, "__LINKED_VALUE");
    }

    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT
        builder, Object value) {
        // override in concrete implementation
    }
}
