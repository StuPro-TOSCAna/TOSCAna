package org.opentosca.toscana.plugins.kubernetes.docker.image;

import java.util.Collections;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.kubernetes.docker.util.DockerRegistryCredentials;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.auth.FixedRegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;

/**
 This ImageBuilder Implementation takes the built images and pushes them to a given registry.
 */
public class PushingImageBuilder extends ImageBuilder {
    /**
     The Credentials to the Docker Registry (including the URL)
     */
    private final DockerRegistryCredentials credentials;

    /**
     @param credentials the Credentials for the Registry you want to push to
     @param tag         the tag of the image. It is important to note that this should not contain
     Registry URL, Username and Repository these will be appended from the DockerRegistryCredentials given
     */
    public PushingImageBuilder(
        DockerRegistryCredentials credentials,
        String tag,
        String dockerWorkDir,
        TransformationContext context
    ) {
        super(tag, dockerWorkDir, context);
        this.credentials = credentials;
    }

    @Override
    public String getTag() {
        String registryUrl = credentials.getRegistryURL();
        if (!registryUrl.endsWith("/")) {
            registryUrl = registryUrl + "/";
        }
        String username = credentials.getUsername();
        if (!username.isEmpty()) {
            username += "/";
        }
        String tag = String.format(
            "%s%s%s:%s",
            registryUrl,
            username,
            credentials.getRepository(),
            super.getTag()
        );

        logger.info("The tag of the image built is '{}'", tag);

        return tag;
    }

    @Override
    protected DockerClient getDockerClient() throws DockerCertificateException {
        // Initialize Docker Client
        logger.debug("Attempting to get connection to the Docker Daemon");
        RegistryAuth auth = this.credentials.toRegistryAuth();
        return DefaultDockerClient.fromEnv()
            .registryAuthSupplier(
                new FixedRegistryAuthSupplier(
                    auth,
                    RegistryConfigs.create(
                        Collections.singletonMap(this.credentials.getRegistryURL(), auth)
                    )
                )
            )
            .build();
    }

    @Override
    public void storeImage() throws Exception {
        DockerClient client = getDockerClient();

        client.push(getTag(), this);
    }
}
