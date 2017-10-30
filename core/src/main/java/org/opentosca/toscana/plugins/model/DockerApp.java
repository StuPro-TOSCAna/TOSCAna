package org.opentosca.toscana.plugins.model;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerApp {
    private final static Logger logger = LoggerFactory.getLogger(DockerApp.class);
    private final String[] tag;
    private final String[] identifier;
    private final List<String> dependencies;
    private final TNodeTemplate tNodeTemplate;

    public DockerApp(TNodeTemplate tNodeTemplate) {
        tag = new String[2];
        identifier = new String[2];
        dependencies = new LinkedList<>();
        this.tNodeTemplate = tNodeTemplate;
    }

    public String[] getTag() {
        return tag;
    }

    public String[] getIdentifier() {
        return identifier;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void parseNodeTemplate() throws InvalidDockerAppException {
        logger.info("Parsing DockerApp NodeTemplate.");
        parseAttributes();
        parseDependencies();
    }

    private void parseDependencies() throws InvalidDockerAppException {
        logger.info("Parsing DockerApp dependencies.");
        tNodeTemplate.getInterfaces()
            .get("Standard")
            .getOperations().get("create")
            .getImplementation()
            .getDependencies()
            .forEach(qName -> dependencies.add(qName.getLocalPart()));
        if (dependencies.size() == 0) throw new InvalidDockerAppException("Parsing the dependencies failed.");
    }

    private void parseAttributes() {
        logger.info("Parsing DockerApp attributes.");
        tNodeTemplate.getAttributes().forEach((s, tAttributeAssignment) -> {
            if ("identifier".equals(s)) {
                identifier[0] = tAttributeAssignment.getDescription();
                identifier[1] = (String) tAttributeAssignment.getValue();
            } else if (s.equals("tag")) {

                tag[0] = tAttributeAssignment.getDescription();
                tag[1] = (String) tAttributeAssignment.getValue();
                System.out.println(tag[0]);
            }
        });
    }
}
