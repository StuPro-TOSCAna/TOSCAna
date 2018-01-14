package org.opentosca.toscana.core.parse.graphconverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EntityId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

public class ServiceModel {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private ServiceGraph graph;

    public ServiceModel(File template, Log log) {
        this(template);
        this.logger = log.getLogger(getClass());
    }

    public ServiceModel(File template) {
        Yaml yaml = new Yaml();
        try {
            Node snakeNode = yaml.compose(new FileReader(template));
            this.graph = new ServiceGraph(snakeNode);
            ToscaStructure.buildBasicStructure(this.graph); // in case this has not already been established
            GraphNormalizer.normalize(this);
            new LinkResolver(this).resolveLinks();
        } catch (FileNotFoundException e) {
            // TODO handle error
            e.printStackTrace();
        }
    }

    /**
     Returns the value associated with the entity matching given context.
     */
    public <T> T get(List<String> context) {
        // TODO handle unsafe use of generics
        Optional<BaseEntity<T>> target = graph.getEntity(context);
        if (target.isPresent()) {
            return target.get().get();
        } else {
            throw new IllegalStateException(String.format("No BaseEntity found for context %s", context));
        }
    }

    public <T> Optional<BaseEntity<T>> getEntity(List<String> context) {
        return graph.getEntity(context);
    }

    public <T> Optional<BaseEntity<T>> getEntity(EntityId context) {
        return graph.getEntity(context);
    }

    public <T> void add(BaseEntity<T> entity) {
        graph.addEntity(entity);
    }

    /**
     Returns an iterator for a set of entities referenced by given EntityId.
     */
    // TODO what if EntityId describes a ScalarEntity/ SequenceEntity?
    public Iterator<MappingEntity> iterator(EntityId id) {
        MappingEntity entities = (MappingEntity) this.<Map<String, String>>getEntity(id).get();
        Set<MappingEntity> map = entities.getChildren().stream()
            .map(e -> (MappingEntity) e)
            .collect(Collectors.toSet());
        return map.iterator();
    }

    public Map<String,Property> getInputs() {
        // TODO add general mechanism for handling inputs
        return null;
    }
    
    public ServiceGraph getGraph() {
        return graph;
    }

}
