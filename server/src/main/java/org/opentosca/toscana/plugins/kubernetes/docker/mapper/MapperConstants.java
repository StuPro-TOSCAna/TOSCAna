package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.capability.OsCapability.Architecture;

import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_32;
import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_64;

public class MapperConstants {
    public static final String DEFAULT_IMAGE_PATH = "library/ubuntu:latest";
    public static final String DEFAULT_IMAGE_DISTRO = "ubuntu";
    public static final String DOCKER_HUB_URL = "https://registry.hub.docker.com";
    public static final Map<Architecture, String> ARCHITECTURE_MAP;

    static {
        HashMap<Architecture, String> archMap = new HashMap<>();

        //Map TOSCA Architecture strings to Docker architecture description strings
        archMap.put(x86_32, "i386");
        archMap.put(x86_64, "amd64");

        ARCHITECTURE_MAP = Collections.unmodifiableMap(archMap);
    }
}
