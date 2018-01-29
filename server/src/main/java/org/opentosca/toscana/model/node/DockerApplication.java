package org.opentosca.toscana.model.node;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.requirement.ContainerHostRequirement;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DockerApplication extends ContainerApplication {

    public static ToscaKey<DockerHostRequirement> DOCKER_HOST = new RequirementKey<>("host")
        .subTypes(DockerContainerCapability.class, ContainerRuntime.class, HostedOn.class)
        .type(DockerHostRequirement.class);

    public DockerApplication(MappingEntity mappingEntity) {
        super(mappingEntity);
        init();
    }

    private void init() {
        setDefault(DOCKER_HOST, new DockerHostRequirement(getChildEntity(DOCKER_HOST)));
    }

    /**
     @return {@link #HOST}
     */
    public DockerHostRequirement getDockerHost() {
        return get(DOCKER_HOST);
    }

    @Override
    public ContainerHostRequirement getHost() {
        throw new UnsupportedOperationException("Use 'getDockerHost()' instead of 'getHost()'");
    }

    /**
     Sets {@link #HOST}
     */
    public DockerApplication setHost(DockerHostRequirement host) {
        set(DOCKER_HOST, host);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
