package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImageTag;

public class MapperUtils {

    public static final Comparator<DockerImageTag> TAG_COMPARATOR_MINOR_VERSION =
        Comparator.comparing(DockerImageTag::toVersion);

    public static boolean anythingSet(OsCapability capability) {
        Optional[] optionals = {
            capability.getDistribution(),
            capability.getArchitecture(),
            capability.getType(),
            capability.getVersion()
        };
        return Arrays.stream(optionals).anyMatch(Optional::isPresent);
    }
}
