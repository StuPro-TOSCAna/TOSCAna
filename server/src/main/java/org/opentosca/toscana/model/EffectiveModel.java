package org.opentosca.toscana.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.CsarEntrypointDetector;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.parse.graphconverter.ServiceModel;
import org.opentosca.toscana.core.parse.graphconverter.ToscaFactory;
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
    private final ServiceModel serviceModel;
    private Map<String, RootNode> nodeMap;
    private Map<String, Property> inputs;

    public EffectiveModel(Csar csar, File csarContentRoot) throws InvalidCsarException {
        Log log = csar.getLog();
        CsarEntrypointDetector entrypointDetector = new CsarEntrypointDetector(log);
        logger = log.getLogger(getClass());
        File template = entrypointDetector.findEntryPoint(csarContentRoot);
        this.serviceModel = new ServiceModel(template, csar.getLog());
        init();
    }

    public EffectiveModel(File template, Log log) {
        logger = LoggerFactory.getLogger(getClass());
        this.serviceModel = new ServiceModel(template, log);
        init();
    }

    private void init() {
        nodeMap = ToscaFactory.wrapNodes(serviceModel);
        nodeMap.forEach((name, node) -> topology.addVertex(node));
        initEdges();
        inputs = serviceModel.getInputs();
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
    }

    public Set<RootNode> getNodes() {
        return topology.vertexSet();
    }

    public Map<String, RootNode> getNodeMap() {
        return Collections.unmodifiableMap(nodeMap);
    }

    public Graph<RootNode, RootRelationship> getTopology() {
        return topology;
    }

    public Map<String, Property> getInputs() {
        return inputs;
    }
}

