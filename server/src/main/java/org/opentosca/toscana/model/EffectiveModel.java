package org.opentosca.toscana.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.EntrypointDetector;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.parse.converter.TypeWrapper;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffectiveModel {

    private final Logger logger;

    private final Graph<RootNode, RootRelationship> topology =
        new DefaultDirectedGraph<>(RootRelationship.class);
    private final ServiceGraph serviceGraph;
    private Map<String, RootNode> nodeMap;
    private boolean initialized = false;

    protected EffectiveModel(Csar csar) throws InvalidCsarException {
        Log log = csar.getLog();
        EntrypointDetector entrypointDetector = new EntrypointDetector(log);
        this.logger = log.getLogger(getClass());
        File template = entrypointDetector.findEntryPoint(csar.getContentDir());
        this.serviceGraph = new ServiceGraph(template, csar.getLog());
    }

    protected EffectiveModel(File template, Log log) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.serviceGraph = new ServiceGraph(template, log);
    }

    private void init() {
        if (!serviceGraph.requiredInputsSet()) {
            throw new IllegalStateException("Must not initialize backing model: Not all required inputs are set");
        }
        nodeMap = TypeWrapper.wrapNodes(serviceGraph);
        nodeMap.forEach((name, node) -> topology.addVertex(node));
        initEdges();
    }

    private void initEdges() {
        for (RootNode node : topology.vertexSet()) {
            for (Requirement<?, ?, ?> requirement : node.getRequirements()) {
                Set<? extends RootNode> fulfillers = requirement.getFulfillers();
                for (RootNode fulfiller : fulfillers) {
                    topology.addEdge(node, fulfiller, requirement.get(requirement.RELATIONSHIP));
                }
            }
        }
        initialized = true;
    }

    public Set<RootNode> getNodes() {
        if (!initialized) {
            init();
        }
        return topology.vertexSet();
    }

    public Map<String, RootNode> getNodeMap() {
        if (!initialized) {
            init();
        }
        return Collections.unmodifiableMap(nodeMap);
    }

    public Graph<RootNode, RootRelationship> getTopology() {
        if (!initialized) {
            init();
        }
        return topology;
    }

    public Map<String, Property> getInputs() {
        return serviceGraph.getInputs();
    }
}

