package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.capability.ContainerCapabilityVisitor;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.Dbms.DbmsBuilder;

import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class DbmsVisitor<NodeT extends Dbms, BuilderT extends DbmsBuilder> extends SoftwareComponentVisitor<NodeT, BuilderT> {

    private static final String ROOT_PASSWORD_PROPERTY = "root_password";

    private static final String HOST_CAPABILITY = "host";

    private static final String PORT_PROPERTY = "port";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case ROOT_PASSWORD_PROPERTY:
                builder.rootPassword((String) value);
                break;
            case PORT_PROPERTY:
                builder.port((Integer) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected void handleCapability(TCapabilityAssignment node, BuilderT builder, String key) {
        switch (key) {
            case HOST_CAPABILITY:
                builder.containerHost(new ContainerCapabilityVisitor<>().handle(node));
                break;
            default:
                super.handleCapability(node, builder, key);
                break;
        }
    }
}
