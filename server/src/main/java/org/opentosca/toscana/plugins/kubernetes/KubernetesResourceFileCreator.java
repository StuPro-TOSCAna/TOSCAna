package org.opentosca.toscana.plugins.kubernetes;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.DeploymentBuilder;
import io.fabric8.kubernetes.client.internal.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesResourceFileCreator {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesResourceFileCreator.class);

    public String createResourceFileAsString(String appName) throws JsonProcessingException {
        String service = createService(appName);
        String deployment = createDeployment(appName);
        return service + deployment;
    }

    private String createService(String appName) throws JsonProcessingException {
        Service service = new ServiceBuilder()
            .withNewMetadata()
            .withName(appName + "-service")
            .addToLabels("app", appName)
            .endMetadata()
            .withNewSpec()
            .addNewPort()
            .withPort(80)
            .endPort()
            .addToSelector("app", appName)
            .withType("NodePort")
            .endSpec()
            .build();
        logger.info("Service resource created.");
        return toYaml(service);
    }

    private String createDeployment(String appName) throws JsonProcessingException {
        Deployment deployment = new DeploymentBuilder()
            .withNewMetadata()
            .withName(appName + "-deployment")
            .endMetadata()
            .withNewSpec()
            .withNewSelector()
            .addToMatchLabels("app", appName)
            .endSelector()
            .withNewTemplate()
            .withNewMetadata()
            .addToLabels("app", appName)
            .endMetadata()
            .withNewSpec()
            .addNewContainer()
            .withName(appName)
            .withImage("username/" + appName)
            .addNewPort()
            .withContainerPort(80)
            .endPort()
            .endContainer()
            .endSpec()
            .endTemplate()
            .endSpec().build();
        logger.info("Deployment resource created.");
        return toYaml(deployment);
    }

    public String toYaml(HasMetadata obj) throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(obj);
    }
}
