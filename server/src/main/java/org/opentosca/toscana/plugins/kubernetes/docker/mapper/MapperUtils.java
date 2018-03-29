package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImageTag;

public class MapperUtils {

    /**
     This Comparator is used to sort versioned image Tags by version
     */
    public static final Comparator<DockerImageTag> TAG_COMPARATOR_MINOR_VERSION =
        Comparator.comparing(DockerImageTag::toVersion);

    /**
     Checks if the OSCapability has anything set.
     That means the method returns false if the osCapability is empty and does not define anything
     */
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
