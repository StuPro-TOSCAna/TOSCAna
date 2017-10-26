package org.opentosca.toscana.plugins.kubernetes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.model.DockerApp;
import org.opentosca.toscana.plugins.model.InvalidDockerAppException;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KubernetesPlugin extends AbstractPlugin {
    private final static Logger logger = LoggerFactory.getLogger(KubernetesPlugin.class);
    private PluginFileAccess fileAccess;

    @Override
    public String getName() {
        return "Kubernetes";
    }

    @Override
    public String getIdentifier() {
        return "kubernetes";
    }

    @Override
    public void transform(TransformationContext context) throws Exception {
        logger.info("Started transformation to kubernetes artefact.");
        fileAccess = context.getPluginFileAccess();
        DockerApp dockerApp = getDockerApp(context.getServiceTemplate());
        String appName = dockerApp.getTag()[1];
        String resourceFile = new KubernetesResourceFileCreator().createResourceFileAsString(appName);
        String manual = KubernetesManualCreator.createManual(appName, (appName + "_resource.yaml"));
        String resourceFilePath = "/" + appName + "_resource.yaml";
        String manualPath = "/Readme.md";
        InputStream inResourceFile = new ByteArrayInputStream(resourceFile.getBytes("UTF-8"));
        InputStream inManual = new ByteArrayInputStream(manual.getBytes("UTF-8"));
        fileAccess.write(manualPath, inManual);
        fileAccess.write(resourceFilePath, inResourceFile);
        List<String> dockerFilePaths = dockerApp.getDependencies();
        for (int i = 0; i < dockerFilePaths.size(); i++) {
            fileAccess.copy(dockerFilePaths.get(i));
        }
    }

    public DockerApp getDockerApp(TServiceTemplate template) throws InvalidDockerAppException {
        logger.info(template.toString());
        TTopologyTemplateDefinition tTopologyTemplateDefinition = template.getTopologyTemplate();

        TNodeTemplate dockerAppNodeTemplate = findDockerApp(tTopologyTemplateDefinition);
        DockerApp app = new DockerApp(dockerAppNodeTemplate);
        app.parseNodeTemplate();

        return app;
    }

    private TNodeTemplate findDockerApp(TTopologyTemplateDefinition tTopologyTemplateDefinition) throws InvalidDockerAppException {
        final TNodeTemplate[] template = new TNodeTemplate[1];
        Map<String, TNodeTemplate> tNodeTemplateMap = tTopologyTemplateDefinition.getNodeTemplates();

        tNodeTemplateMap.forEach((String s, TNodeTemplate tNodeTemplate) -> {
            if (tNodeTemplate.getType().getLocalPart().equals("DockerApp")) {
                template[0] = tNodeTemplate;
            }
        });
        if (template[0] == null) throw new InvalidDockerAppException("DockerApp in given TServiceTemplate not found.");
        return template[0];
    }
}
