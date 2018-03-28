package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.capability.OsCapability.Architecture;

import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_32;
import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_64;

public class MapperConstants {
    /**
     Defines the default that should be used if a empty (or almost empty Linux OsCapability) is given
     <p>
     The Default value is <code>library/ubuntu:latest</code>
     */
    public static final String DEFAULT_IMAGE_PATH = "library/ubuntu:latest";
    public static final String DEFAULT_IMAGE_DISTRO = "ubuntu";
    /**
     Stores the default URL to DockerHub
     */
    public static final String DOCKER_HUB_URL = "https://registry.hub.docker.com";
    /**
     This map is used to Map the OsCapabiliy Architecture Enum to docker Specific architecture strings
     The System Currently only supports x64 (64 Bit aka. amd64) and x86 (32 Bit aka. i386) based architectures
     */
    public static final Map<Architecture, String> ARCHITECTURE_MAP;

    static {
        //Initialize the ARCHITECTURE MAP constant field
        HashMap<Architecture, String> archMap = new HashMap<>();

        //Map TOSCA Architecture strings to Docker architecture description strings
        archMap.put(x86_32, "i386");
        archMap.put(x86_64, "amd64");

        ARCHITECTURE_MAP = Collections.unmodifiableMap(archMap);
    }
}
