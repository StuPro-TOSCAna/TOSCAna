package org.opentosca.toscana.core.parse.converter.visitor.node;

import java.util.Map;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.SoftwareComponent.SoftwareComponentBuilder;
import org.opentosca.toscana.model.relation.HostedOn;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

import static org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition.ADMIN_CREDENTIAL_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition.COMPONENT_VERSION_PROPERTY;
import static org.opentosca.toscana.model.nodedefinition.SoftwareComponentDefinition.HOST_REQUIREMENT;

public class SoftwareComponentVisitor<NodeT extends SoftwareComponent, BuilderT extends SoftwareComponentBuilder> extends RootNodeVisitor<NodeT, BuilderT> {

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case ADMIN_CREDENTIAL_PROPERTY:
                // TODO implement proper credential handling
                Map<String, String> credentialMap = (Map<String, String>) value;
                Credential credential = Credential
                    .builder(credentialMap.get("token"))
                    .user(credentialMap.get("user"))
                    .build();
                builder.adminCredential(credential);
                break;
            case COMPONENT_VERSION_PROPERTY:
                String componentVersion = (String) value;
                builder.componentVersion(componentVersion);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        switch (context.getKey()) {
            case HOST_REQUIREMENT:
                builder.host(provideRequirement(requirement, context, HostedOn.class));
                break;
            default:
                super.handleRequirement(requirement, context, builder);
                break;
        }
    }
}
