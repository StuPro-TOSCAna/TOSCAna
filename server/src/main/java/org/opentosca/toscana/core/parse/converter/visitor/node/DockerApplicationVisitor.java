package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.DockerApplication.DockerApplicationBuilder;
import org.opentosca.toscana.model.relation.HostedOn;

import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

public class DockerApplicationVisitor<NodeT extends DockerApplication, BuilderT extends DockerApplicationBuilder> extends ContainerApplicationVisitor<NodeT, BuilderT> {

    private static final String HOST_REQUIREMENT = "host";

    @Override
    protected void handleRequirement(TRequirementAssignment node, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.dockerHost(provideRequirement(node, context, HostedOn.class));
                break;
            default:
                super.visit(node, context);
                break;
        }
    }
}
