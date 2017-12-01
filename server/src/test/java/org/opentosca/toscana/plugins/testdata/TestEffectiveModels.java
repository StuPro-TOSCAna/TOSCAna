package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DockerContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.Requirement;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.StorageCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.relation.HostedOn;
import org.opentosca.toscana.model.relation.RootRelationship;

import com.google.common.collect.Sets;

public class TestEffectiveModels {

    public static EffectiveModel getSingleComputeNodeModel() {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        ContainerCapability.ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(1024)
            .diskSizeInMB(2000)
            .numCpus(1)
            .name("host")
            .validSourceTypes(validSourceTypes);

        ContainerCapability containerCapability = containerCapabilityBuilder.build();

        AdminEndpointCapability computeAdminEndpointCap = AdminEndpointCapability
            .builder("127.0.0.1")
            .port(new Port(80)).build();
        ScalableCapability scalableCapability = ScalableCapability.builder(Range.EXACTLY_ONCE).build();
        BindableCapability bindableCapability = BindableCapability.builder().build();
        //HostedOn hostedOnCompute = HostedOn.builder().build();
        AttachesTo attachesTo = AttachesTo.builder("mount").build();
        AttachmentCapability attachmentCapability = AttachmentCapability.builder().build();
        //BlockStorage blockStorage = BlockStorage.builder("storage", "1", attachmentCapability).build();

        Requirement<AttachmentCapability, BlockStorage, AttachesTo> computeRequirement =
            Requirement.<AttachmentCapability, BlockStorage, AttachesTo>builder(attachmentCapability, attachesTo)
                .build();
        OsCapability os = OsCapability.builder().type(OsCapability.Type.WINDOWS).build();
        Compute computeNode = Compute.builder("server", os, computeAdminEndpointCap, scalableCapability,
            bindableCapability, computeRequirement).host(containerCapability).build();
        return new EffectiveModel(Sets.newHashSet(computeNode));
    }

    public static EffectiveModel getMinimalDockerModel() {
        DockerContainerCapability containerCapability = DockerContainerCapability.builder().name("host").build();
        ScalableCapability scalableCapability = ScalableCapability.builder(Range.EXACTLY_ONCE).build();
        Requirement<ContainerCapability, Compute, HostedOn> requirement = Requirement.<ContainerCapability, Compute, 
            HostedOn>builder(containerCapability, HostedOn.builder().build()).build();
        ContainerRuntime dockerRuntime
            = ContainerRuntime.builder("dockerRuntime", requirement ,
            containerCapability, scalableCapability).build();
        HostedOn hostedOn = HostedOn.builder().build();
        Requirement<DockerContainerCapability, ContainerRuntime, HostedOn> host
            = Requirement.<DockerContainerCapability, ContainerRuntime, HostedOn>builder(containerCapability, hostedOn)
            .fulfiller(dockerRuntime)
            .build();
        EndpointCapability endpointCapability = EndpointCapability.builder("127.0.0.1", new Port(80)).build();
        Requirement<EndpointCapability, RootNode, RootRelationship> network
            = Requirement.<EndpointCapability, RootNode, RootRelationship>builder(endpointCapability, hostedOn).build();
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
