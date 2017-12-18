package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.util.BuilderUtil;
import org.opentosca.toscana.core.parse.converter.visitor.AbstractEntityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.Capability.CapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;

public abstract class CapabilityVisitor<CapabilityT extends Capability, BuilderT extends CapabilityBuilder> extends AbstractEntityVisitor<CapabilityT, BuilderT> {

    public CapabilityT handle(TCapabilityAssignment assignment) {
        Context<BuilderT> context = new Context<>(BuilderUtil.newInstance(getBuilderClass()));
        ConversionResult<CapabilityT> result = visit(assignment, context);
        return result.getResult();
    }

    @Override
    public ConversionResult<CapabilityT> visit(TCapabilityAssignment node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        super.visit(node, parameter);
        return new ConversionResult<CapabilityT>((CapabilityT) builder.build());
    }

    abstract protected Class getBuilderClass();
}
