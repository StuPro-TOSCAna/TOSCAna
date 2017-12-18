package org.opentosca.toscana.core.parse.converter.visitor.capability;

import java.util.List;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.DockerContainerCapability.DockerContainerCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class DockerContainerCapabilityVisitor<CapabilityT extends DockerContainerCapability, BuilderT extends DockerContainerCapabilityBuilder> extends ContainerCapabilityVisitor<CapabilityT, BuilderT> {

    private final static String VERSIONS_PROPERTY = "versions";
    private final static String PUBLISH_ALL_PROPERTY = "publish_all";
    private final static String PUBLISH_PORTS_PROPERTY = "publish_ports";
    private final static String EXPOSE_PORTS_PROPERTY = "expose_ports";
    private final static String VOLUMES_PROPERTY = "volumes";
    private final static String HOST_ID_PROPERTY = "host_id";
    private final static String VOLUME_ID_PROPERTY = "volume_id";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case VERSIONS_PROPERTY:
                builder.versions((List<String>) value);
                break;
            case PUBLISH_ALL_PROPERTY:
                builder.publishAll((Boolean) value);
                break;
            case PUBLISH_PORTS_PROPERTY:
                // TODO add portspec conversion
                throw new UnsupportedOperationException();
            case EXPOSE_PORTS_PROPERTY:
                // TODO add portspec conversion
                throw new UnsupportedOperationException();
            case VOLUMES_PROPERTY:
                builder.volumes((List<String>) value);
                break;
            case HOST_ID_PROPERTY:
                builder.hostId((String) value);
                break;
            case VOLUME_ID_PROPERTY:
                builder.volumeId((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return DockerContainerCapabilityBuilder.class;
    }
}
