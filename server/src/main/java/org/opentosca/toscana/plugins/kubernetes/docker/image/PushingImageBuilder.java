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

public class PushingImageBuilder extends ImageBuilder {
    private final DockerRegistryCredentials credentials;

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
        String tag = String.format(
            "%s%s/%s:%s",
            credentials.getRegistryURL(),
            credentials.getUsername(),
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
        //client.auth(credentials.toRegistryAuth());

        client.push(getTag(), this);
    }
}
