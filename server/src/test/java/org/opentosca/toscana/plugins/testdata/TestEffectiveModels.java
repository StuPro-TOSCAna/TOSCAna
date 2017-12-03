package org.opentosca.toscana.plugins.testdata;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.Requirement;

import com.google.common.collect.Sets;

public class TestEffectiveModels {

    public static EffectiveModel getMinimalDockerModel() {
        DockerContainerCapability containerCapability = DockerContainerCapability.builder().name("host").build();
        ScalableCapability scalableCapability = ScalableCapability.builder(Range.EXACTLY_ONCE).build();
        HostRequirement requirement = HostRequirement.builder(containerCapability, HostedOn.builder().build()).build();
        ContainerRuntime dockerRuntime
            = ContainerRuntime.builder("dockerRuntime", requirement,
            containerCapability, scalableCapability).build();
        HostedOn hostedOn = HostedOn.builder().build();
        DockerHostRequirement host = DockerHostRequirement.builder(containerCapability, hostedOn)
            .fulfiller(dockerRuntime)
            .build();
        EndpointCapability endpointCapability = EndpointCapability.builder("127.0.0.1", new Port(80)).build();
        EndpointRequirement network = EndpointRequirement.builder(endpointCapability, hostedOn).build();
        StorageCapability storageCapability = StorageCapability.builder().build();
        AttachesTo attachesTo = AttachesTo.builder("/").build();
        Requirement<StorageCapability, RootNode, RootRelationship> storage
            = Requirement.<StorageCapability, RootNode, RootRelationship>builder(storageCapability, attachesTo).build();
        DockerApplication simpleTaskApp
            = DockerApplication.builder(host, "simpleTaskApp", network, storage).build();
        return new EffectiveModel(Sets.newHashSet(simpleTaskApp, dockerRuntime));
    }

    public static EffectiveModel getLampModel() {
        return new EffectiveModel(new LampApp().getLampApp());
    }
}
