package org.opentosca.toscana.plugins.kubernetes;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.TOSCAnaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PlatformInput;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KubernetesPlugin extends TOSCAnaPlugin<KubernetesLifecycle> {
    public static final String DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY = "docker_push_to_registry";
    public static final String DOCKER_REGISTRY_URL_PROPERTY_KEY = "docker_registry_url";
    public static final String DOCKER_REGISTRY_USERNAME_PROPERTY_KEY = "docker_registry_username";
    public static final String DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY = "docker_registry_repository";
    public static final String DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY = "docker_registry_password";

    private final static Logger logger = LoggerFactory.getLogger(KubernetesPlugin.class);

    private final BaseImageMapper mapper;

    @Autowired
    public KubernetesPlugin(BaseImageMapper mapper) {
        super(getPlatformDetails());
        this.mapper = mapper;
    }

    private static Platform getPlatformDetails() {
        String platformId = "kubernetes";
        String platformName = "Kubernetes";
        Set<PlatformInput> platformProperties = new HashSet<>();
        platformProperties.add(new PlatformInput(
                DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY,
                PropertyType.BOOLEAN,
                "Set this to true if the created docker images should be pushed to the given docker registry",
                true,
                "false"
            )
        );
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
    protected KubernetesLifecycle getInstance(TransformationContext context) throws Exception {
        return new KubernetesLifecycle(context, mapper);
    }
}
