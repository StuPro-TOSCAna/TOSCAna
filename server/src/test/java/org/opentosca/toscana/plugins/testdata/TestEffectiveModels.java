package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.requirement.DockerHostRequirement;

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

        OsCapability os = OsCapability.builder()
            .type(OsCapability.Type.WINDOWS)
            .build();
        Compute computeNode = Compute
            .builder("server")
            .os(os)
            .host(host)
            .build();
        return new EffectiveModel(Sets.newHashSet(computeNode), new HashSet<>());
    }

    public static EffectiveModel getMinimalDockerModel() {
        ContainerRuntime dockerRuntime = ContainerRuntime
            .builder("dockerRuntime")
            .build();
        DockerHostRequirement host = DockerHostRequirement
            .builder()
            .fulfiller(dockerRuntime)
            .build();
        DockerApplication simpleTaskApp = DockerApplication
            .builder("simpleTaskApp")
            .dockerHost(host)
            .build();
        return new EffectiveModel(Sets.newHashSet(simpleTaskApp, dockerRuntime), new HashSet<>());
    }

    public static EffectiveModel getLampModel() {
        return LampApp.getLampApp();
    }
}
