package org.opentosca.toscana.model.node;

import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.StorageRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class DockerApplication extends ContainerApplication {

    // public access due to hiding of parent field (and therefore getter conflicts..)
    @Getter(AccessLevel.NONE)
    public final DockerHostRequirement host;

    @Builder
    private DockerApplication(DockerHostRequirement host,
                              StorageRequirement storage,
                              EndpointRequirement network,
                              String nodeName,
                              StandardLifecycle standardLifecycle,
                              String description) {
        super(storage, network, nodeName, standardLifecycle, description);
        this.host = DockerHostRequirement.getFallback(host);
        requirements.add(this.host);
    }

    /**
     @param nodeName {@link #nodeName}
     @param network  {@link #network}
     */
    public static DockerApplicationBuilder builder(String nodeName,
                                                   EndpointRequirement network) {
        return new DockerApplicationBuilder()
            .nodeName(nodeName)
            .network(network);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class DockerApplicationBuilder extends ContainerApplicationBuilder {
    }
}
