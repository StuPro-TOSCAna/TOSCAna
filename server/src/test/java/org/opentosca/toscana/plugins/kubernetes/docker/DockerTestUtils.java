package org.opentosca.toscana.plugins.kubernetes.docker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

public class DockerTestUtils {
    public static boolean isDockerAvailable() {
        DockerClient client = null;
        try {
            client = DefaultDockerClient.fromEnv().build();
            if (client == null) {
                return false;
            }
            if (!client.ping().equals("OK")) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
