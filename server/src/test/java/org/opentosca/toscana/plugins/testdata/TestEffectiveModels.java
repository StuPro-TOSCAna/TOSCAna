package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;
import org.opentosca.toscana.model.requirement.EndpointRequirement;
import org.opentosca.toscana.model.requirement.StorageRequirement;

import com.google.common.collect.Sets;

public class TestEffectiveModels {

    public static EffectiveModel getSingleComputeNodeModel() {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        ContainerCapability host = ContainerCapability.builder()
            .memSizeInMB(1024)
            .diskSizeInMB(2000)
            .numCpus(1)
            .validSourceTypes(validSourceTypes)
            .build();

        AdminEndpointCapability computeAdminEndpointCap = AdminEndpointCapability
            .builder("127.0.0.1", new Port(80))
            .build();
        AttachesTo attachesTo = AttachesTo
            .builder("mount")
            .build();
        BlockStorageRequirement blockStorageRequirement = BlockStorageRequirement
            .builder(attachesTo)
            .build();
        OsCapability os = OsCapability.builder()
            .type(OsCapability.Type.WINDOWS)
            .build();
        Compute computeNode = Compute
            .builder("server", os, computeAdminEndpointCap, blockStorageRequirement)
            .host(host)
            .build();
        return new EffectiveModel(Sets.newHashSet(computeNode));
    }

    public static EffectiveModel getMinimalDockerModel() {
        DockerContainerCapability containerCapability = DockerContainerCapability.builder().build();
        ContainerRuntime dockerRuntime = ContainerRuntime
            .builder("dockerRuntime").
                containerHost(containerCapability).
                build();
        DockerHostRequirement host = DockerHostRequirement
            .builder()
            .fulfiller(dockerRuntime)
            .build();
        EndpointCapability endpointCapability = EndpointCapability
            .builder("127.0.0.1", new Port(80))
            .build();
        EndpointRequirement network = EndpointRequirement.
            builder(endpointCapability)
            .build();
        AttachesTo attachesTo = AttachesTo
            .builder("/")
            .build();
        StorageRequirement storage = StorageRequirement
            .builder(attachesTo)
            .build();
        DockerApplication simpleTaskApp = DockerApplication
            .builder("simpleTaskApp", network)
            .host(host)
            .build();
        return new EffectiveModel(Sets.newHashSet(simpleTaskApp, dockerRuntime));
    }

    public static EffectiveModel getLampModel() {
        return new EffectiveModel(new LampApp().getLampApp());
    }
}
