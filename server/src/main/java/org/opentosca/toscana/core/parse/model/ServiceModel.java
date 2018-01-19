package org.opentosca.toscana.core.parse.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EntityId;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

public class ServiceModel {

    private final Log log;
    private final Logger logger;
    private final ServiceGraph graph;

    public ServiceModel(File template, Log log) {
        this.log = log;
        this.logger = log.getLogger(getClass());
        Yaml yaml = new Yaml();
        try {
            Node snakeNode = yaml.compose(new FileReader(template));
            this.graph = new ServiceGraph(snakeNode, log);
        } catch (FileNotFoundException e) {
            logger.error(String.format("Template '%s' does not exist - failed to construct ServiceModel", template), e);
            throw new IllegalStateException();
        }
    }

    public Optional<Entity> getEntity(List<String> context) {
        return graph.getEntity(context);
    }

    public Optional<Entity> getEntity(EntityId context) {
        return graph.getEntity(context);
    }

    public Entity getEntityOrThrow(EntityId entityId) {
        return graph.getEntityOrThrow(entityId);
    }

    public void addEntity(Entity entity) {
        graph.addEntity(entity);
    }

    public ServiceGraph getGraph() {
        return graph;
    }

    public Map<String, Property> getInputs() {
        return graph.getInputs();
    }
}
