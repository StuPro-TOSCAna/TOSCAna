package org.opentosca.toscana.plugins.kubernetes.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.testdata.TestCsars;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.InputProperty;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.EffectiveModelFactory;
import org.opentosca.toscana.plugins.util.TestUtil;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerMount;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_URL_PROPERTY_KEY;
import static org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin.DOCKER_REGISTRY_USERNAME_PROPERTY_KEY;

public class KubernetesPushingGopherIT extends KubernetesLampIT {

    private static final Logger logger = LoggerFactory.getLogger(KubernetesPushingGopherIT.class);

    private static final String EXPECTED_TAG = "127.0.0.1:%d/test/gopher:app";

    private List<String> runningContainers = new ArrayList<>();

    private int registryPort;

    public KubernetesPushingGopherIT() throws Exception {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        DockerClient client = DefaultDockerClient.fromEnv().build();
        logger.info("Downloading registry image");
        client.pull("registry:2");

        this.registryPort = 5000;

        final Map<String, List<PortBinding>> ports = singletonMap(
            "5000/tcp", Collections.singletonList(PortBinding.of("0.0.0.0", this.registryPort)));
        final HostConfig hostConfig = HostConfig.builder().portBindings(ports)
            .build();

        logger.info("Creating Local Registry Container");
        ContainerConfig config = ContainerConfig.builder()
            .hostConfig(hostConfig)
            .image("registry:2").build();
        String id = client.createContainer(config).id();
        logger.info("Registry container id: {}", id);

        logger.info("Starting registry container");
        client.startContainer(id);
        this.runningContainers.add(id);
    }

    @Override
    protected void onSuccess(File outputDir) throws Exception {
        DockerClient client = DefaultDockerClient.fromEnv().build();
        client.pull(getExpectedTag());

        int containerPort = TestUtil.getRandomOpenPort();

        Map<String, List<PortBinding>> ports = singletonMap(
            "8080/tcp", Collections.singletonList(PortBinding.of("0.0.0.0", containerPort)));
        HostConfig hostConfig = HostConfig.builder().portBindings(ports)
            .build();

        ContainerConfig config = ContainerConfig.builder()
            .hostConfig(hostConfig)
            .image(getExpectedTag()).build();
        String id = client.createContainer(config).id();
        this.runningContainers.add(id);

        client.startContainer(id);

        Thread.sleep(5000);
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(String.format("http://127.0.0.1:%d/", containerPort)).get().build();
        Response response = httpClient.newCall(request).execute();

        Assert.assertEquals(200, response.code());
    }

    @Override
    protected EffectiveModel getModel() {
        return new EffectiveModelFactory().create(TestCsars.VALID_GOPHER_TEMPLATE, logMock());
    }

    @Override
    protected void copyArtifacts(File contentDir) throws Exception {
        File inputDir = new File(getClass().getResource("/csars/yaml/valid/gopher").getFile());
        FileUtils.copyDirectory(inputDir, contentDir);
    }

    @Override
    protected PropertyInstance getInputs(EffectiveModel model) throws NoSuchPropertyException {
        Set<InputProperty> prop = new HashSet<>(plugin.getPlatform().properties);
        prop.addAll(model.getInputs().values());
        PropertyInstance instance = new PropertyInstance(prop, mock(Transformation.class));

        instance.set(DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY, "true");
        instance.set(DOCKER_REGISTRY_USERNAME_PROPERTY_KEY, "test");
        instance.set(DOCKER_REGISTRY_PASSWORD_PROPERTY_KEY, "");
        instance.set(DOCKER_REGISTRY_URL_PROPERTY_KEY, "127.0.0.1:5000/");
        instance.set(DOCKER_REGISTRY_REPOSITORY_PROPERTY_KEY, "gopher");
        return instance;
    }

    @After
    @SuppressWarnings("Duplicates")
    public void tearDown() throws Exception {
        DockerClient client = DefaultDockerClient.fromEnv().build();
        logger.info("Stopping and removing Containers");
        for (String id : runningContainers) {
            List<ContainerMount> mounts = client.inspectContainer(id).mounts();
            client.killContainer(id);
            client.removeContainer(id);
            mounts.forEach(e -> {
                try {
                    client.removeVolume(e.name());
                } catch (DockerException | InterruptedException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            });
        }
        client.removeImage(getExpectedTag());
    }

    private String getExpectedTag() {
        return String.format(EXPECTED_TAG, this.registryPort);
    }
}
