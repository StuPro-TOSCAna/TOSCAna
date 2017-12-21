package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.capability.ContainerCapabilityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.capability.ScalableCapabilityVisitor;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.ContainerRuntime.ContainerRuntimeBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;

import static org.opentosca.toscana.model.nodedefinition.ContainerRuntimeDefinition.HOST_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.ContainerRuntimeDefinition.SCALABLE_CAPABILITY;

public class ContainerRuntimeVisitor<NodeT extends ContainerRuntime, BuilderT extends ContainerRuntimeBuilder> extends SoftwareComponentVisitor<NodeT, BuilderT> {


    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case HOST_CAPABILITY:
                builder.containerHost(new ContainerCapabilityVisitor<>().handle(node));
                break;
            case SCALABLE_CAPABILITY:
                builder.scalable(new ScalableCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }
}
