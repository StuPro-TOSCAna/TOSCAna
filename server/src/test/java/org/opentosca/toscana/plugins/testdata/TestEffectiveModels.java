package org.opentosca.toscana.plugins.testdata;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;

import com.google.common.collect.Sets;

public class TestEffectiveModels {

    public static EffectiveModel getMinimalDockerModel() {
        DockerContainerCapability containerCapability = DockerContainerCapability.builder().name("host").build();
        ScalableCapability scalableCapability = ScalableCapability.builder(Range.EXACTLY_ONCE).build();
        ContainerRuntime dockerRuntime
            = ContainerRuntime.builder("dockerRuntime", containerCapability, scalableCapability).build();
        HostedOn hostedOn = HostedOn.builder().build();
        Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> host
            = Requirement.<DockerContainerCapability, ContainerRuntime, HostedOn>builder(containerCapability, hostedOn)
            .fulfiller(dockerRuntime)
            .build();
        EndpointCapability endpointCapability = EndpointCapability.builder("127.0.0.1", new Port(80)).build();
        Requirement<EndpointCapability, RootNode, RootRelationship> network
            = Requirement.<EndpointCapability, RootNode, RootRelationship>builder(endpointCapability, hostedOn).build();
        StorageCapability storageCapability = StorageCapability.builder().build();
        Requirement<StorageCapability, RootNode, RootRelationship> storage
            = Requirement.<StorageCapability, RootNode, RootRelationship>builder(storageCapability, hostedOn).build();
        DockerApplication simpleTaskApp
            = DockerApplication.builder(host, "simpleTaskApp", network, storage).build();

        return new EffectiveModel(Sets.newHashSet(simpleTaskApp, dockerRuntime));
    }
}
