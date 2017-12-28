package org.opentosca.toscana.core.parse.converter.visitor.capability;

import java.net.MalformedURLException;
import java.net.URL;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.EndpointCapability.EndpointCapabilityBuilder;
import org.opentosca.toscana.model.capability.EndpointCapability.Initiator;
import org.opentosca.toscana.model.datatype.NetworkProtocol;
import org.opentosca.toscana.model.datatype.Port;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class EndpointCapabilityVisitor<CapabilityT extends EndpointCapability, BuilderT extends EndpointCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    private final static String PROTOCOL_PROPERTY = "protocol";
    private final static String PORT_PROPERTY = "port";
    private final static String SECURE_PROPERTY = "secure";
    private final static String URL_PATH_PROPERTY = "url_path";
    private final static String PORT_NAME_PROPERTY = "port_name";
    private final static String NETWORK_NAME_PROPERTY = "network_name";
    private final static String INITIATOR_PROPERTY = "initiator";
    private final static String PORTS_PROPERTY = "ports";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case PROTOCOL_PROPERTY:
                NetworkProtocol protocol = NetworkProtocol.valueOf(((String) value).toUpperCase());
                builder.protocol(protocol);
                break;
            case PORT_PROPERTY:
                builder.port(new Port((Integer) value));
                break;
            case SECURE_PROPERTY:
                builder.secure((Boolean) value);
                break;
            case URL_PATH_PROPERTY:
                try {
                    builder.urlPath(new URL((String) value));
                } catch (MalformedURLException e) {
                    throw new IllegalStateException();
                }
                break;
            case PORT_NAME_PROPERTY:
                builder.portName((String) value);
                break;
            case NETWORK_NAME_PROPERTY:
                builder.networkName((String) value);
                break;
            case INITIATOR_PROPERTY:
                Initiator initiator = Initiator.valueOf(((String) value).toUpperCase());
                builder.initiator(initiator);
                break;
            case PORTS_PROPERTY:
                // TODO implement propper portspec handling
                throw new UnsupportedOperationException();
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return EndpointCapabilityBuilder.class;
    }
}
