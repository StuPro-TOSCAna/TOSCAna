package org.opentosca.toscana.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.EntrypointDetector;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.parse.ToscaTemplateException;
import org.opentosca.toscana.core.parse.converter.TypeWrapper;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.util.LifecyclePhase;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.slf4j.Logger;

public class EffectiveModel {

    private final Log log;
    private final Logger logger;

    private final Graph<RootNode, RootRelationship> topology =
        new DefaultDirectedGraph<>(RootRelationship.class);
    private final ServiceGraph serviceGraph;
    private Map<String, RootNode> nodeMap;
    private boolean initialized = false;

    protected EffectiveModel(Csar csar) throws InvalidCsarException {
        LifecyclePhase parsePhase = csar.getLifecyclePhase(Csar.Phase.PARSE);
        parsePhase.setState(LifecyclePhase.State.EXECUTING);
        this.log = csar.getLog();
        this.logger = log.getLogger(getClass());
        try {
            logger.info("Constructing TOSCA element graph");
            EntrypointDetector entrypointDetector = new EntrypointDetector(log);
            File template = entrypointDetector.findEntryPoint(csar.getContentDir());
            this.serviceGraph = new ServiceGraph(template, csar.getLog());
            parsePhase.setState(LifecyclePhase.State.DONE);
        } catch (Exception e) {
            parsePhase.setState(LifecyclePhase.State.FAILED);
            throw e;
        }
    }

    protected EffectiveModel(File template, Log log) {
        this.log = log;
        this.logger = log.getLogger(getClass());
        this.serviceGraph = new ServiceGraph(template, log);
    }

    private void init() {
        try {
            if (!serviceGraph.inputsValid()) {
                throw new IllegalStateException("Must not initialize backing model: Not all required inputs are set");
            }
            nodeMap = TypeWrapper.wrapNodes(serviceGraph);
            initNodes();
            initEdges();
        } catch (ToscaTemplateException e) {
            logger.error("Given template violates the TOSCA specification", e);
            throw e;
        }
    }

    private void initNodes() {
        logger.info("Populating vertices");
        nodeMap.forEach((name, node) -> {
            logger.info("  > '{}' ({})", name, node.getClass().getSimpleName());
            topology.addVertex(node);
        });
    }

    private void initEdges() {
        logger.info("Populating edges");
        for (RootNode node : topology.vertexSet()) {
            for (Requirement<?, ?, ?> requirement : node.getRequirements()) {
                Set<? extends RootNode> fulfillers = requirement.getFulfillers();
                for (RootNode fulfiller : fulfillers) {
                    RootRelationship relationship = requirement.get(requirement.RELATIONSHIP);
                    logger.info("  > '{}'  === {} ==>> '{}'", node.getEntityName(),
                        relationship.getClass().getSimpleName(), fulfiller.getEntityName());
                    topology.addEdge(node, fulfiller, relationship);
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

    public Map<String,Property> getOutputs() {
        return serviceGraph.getOutputs();
    }
}

