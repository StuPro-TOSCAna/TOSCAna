package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.AdminEndpointCapabilityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.capability.BindableCapabilityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.capability.ContainerCapabilityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.capability.OsCapabilityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.capability.ScalableCapabilityVisitor;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Compute.ComputeBuilder;
import org.opentosca.toscana.model.relation.AttachesTo;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.BINDABLE_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.ENDPOINT_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.HOST_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.LOCAL_STORAGE_REQUIREMENT;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.NETWORKS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.OS_CAPABILITY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PORTS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PRIVATE_ADDRESS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.PUBLIC_ADDRESS_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.ComputeDefinition.SCALABLE_CAPABILITY;

public class ComputeVisitor<NodeT extends Compute, BuilderT extends ComputeBuilder> extends RootNodeVisitor<NodeT, BuilderT> {


    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case PRIVATE_ADDRESS_PROPERTY:
                builder.privateAddress((String) value);
                break;
            case PUBLIC_ADDRESS_PROPERTY:
                builder.publicAddress((String) value);
                break;
            case NETWORKS_PROPERTY:
                // TODO
                throw new UnsupportedOperationException();
            case PORTS_PROPERTY:
                // TODO
                throw new UnsupportedOperationException();
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case HOST_CAPABILITY:
                builder.host(new ContainerCapabilityVisitor<>().handle(node));
                break;
            case ENDPOINT_CAPABILITY:
                builder.endpoint(new AdminEndpointCapabilityVisitor<>().handle(node));
                break;
            case OS_CAPABILITY:
                builder.os(new OsCapabilityVisitor<>().handle(node));
                break;
            case SCALABLE_CAPABILITY:
                builder.scalable(new ScalableCapabilityVisitor<>().handle(node));
                break;
            case BINDABLE_CAPABILITY:
                builder.binding(new BindableCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case LOCAL_STORAGE_REQUIREMENT:
                builder.localStorage(provideRequirement(requirement, context, AttachesTo.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
