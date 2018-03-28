package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.lifecycle.KubernetesLifecycle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 Component Class of the Kubernetes Plugin.
 <p>
 Constructs the KubernetesLifecycle
 */
@Component
public class KubernetesPlugin extends ToscanaPlugin<KubernetesLifecycle> {

    /**
     The Property Key for the Boolean Property that has to get set to true if the user wants to push to a registry
     */
    public static final String DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY = "docker_push_to_registry";
    /**
     The Property Key for the String Property of the Docker Registry URL
     */
    public static final String DOCKER_REGISTRY_URL_PROPERTY_KEY = "docker_registry_url";
    /**
     The Property Key for the Docker Registry Username (Property Type: String)
     */
    public static final String DOCKER_REGISTRY_USERNAME_PROPERTY_KEY = "docker_registry_username";
    /**
     The Property Key for the Docker Registry Password (Property Type: Secret)
     */
    public static final String DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY = "docker_registry_password";
    /**
     The Property Key for the Docker Registry Repository Name (Property Type: String)
     */
    public static final String DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY = "docker_registry_repository";

    /**
     A Reference to the Base Image Mapper
     <p>
     The BaseImageMapper has to be handed through using Spring Dependency injection,
     because it uses Features of the Spring Framework (<code>@Scheduled</code> Methods)
     */
    private final BaseImageMapper mapper;

    @Autowired
    public KubernetesPlugin(BaseImageMapper mapper) {
        super(getPlatformDetails());
        this.mapper = mapper;
    }

    /**
     Constructs the Platform Object for the Kubernetes Plugin
     */
    private static Platform getPlatformDetails() {
        String platformId = "kubernetes";
        String platformName = "Kubernetes";
        Set<PlatformInput> platformProperties = new HashSet<>();
        // Create the "Property Schema" for the Kubernetes Plugin
        // Add the Push Flag (has to be set to true to push)
        platformProperties.add(new PlatformInput(
                DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY,
                PropertyType.BOOLEAN,
                "Set this to true if the created docker images should be pushed to the given docker registry",
                true,
                "false"
            )
        );
        // Add the Registry URL Property (if the value is empty DockerHub gets used internally)
        platformProperties.add(new PlatformInput(
                DOCKER_REGISTRY_URL_PROPERTY_KEY,
                PropertyType.TEXT,
                "The URL To the docker Registry. (Will default to DockerHub if empty)",
                false,
                ""
            )
        );
        platformProperties.add(new PlatformInput(
                DOCKER_REGISTRY_USERNAME_PROPERTY_KEY,
                PropertyType.TEXT,
                "The Username of the user, used to push to the regsitry",
                false
            )
        );
        platformProperties.add(new PlatformInput(
                DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY,
                PropertyType.SECRET,
                "The password of the registry user",
                false
            )
        );
        platformProperties.add(new PlatformInput(
                DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY,
                PropertyType.TEXT,
                "The name of the repository used to push the images onto.",
                false
            )
        );
        return new Platform(platformId, platformName, platformProperties);
    }

    @Override
    public KubernetesLifecycle getInstance(TransformationContext context) throws Exception {
        return new KubernetesLifecycle(context, mapper);
    }
}
