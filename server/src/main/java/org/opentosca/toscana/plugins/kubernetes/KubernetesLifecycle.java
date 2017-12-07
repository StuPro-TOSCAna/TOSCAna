package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;
import org.opentosca.toscana.plugins.kubernetes.visitor.DockerApplicationVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.NodeTypeCheckVisitor;
import org.opentosca.toscana.plugins.kubernetes.visitor.OsCheckNodeVisitor;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import com.fasterxml.jackson.core.JsonProcessingException;

public class KubernetesLifecycle extends AbstractLifecycle {

    private final EffectiveModel model;
    private final PluginFileAccess access;

    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public KubernetesLifecycle(TransformationContext context) throws IOException {
        super(context);
        model = context.getModel();
        access = context.getPluginFileAccess();
    }

    @Override
    public boolean checkModel() {
        Set<RootNode> nodes = model.getNodes();
        boolean nodeTypeCheck = checkNodeTypes(nodes);
        boolean osTypeCheck = checkOsType(nodes);
        return nodeTypeCheck && osTypeCheck;
    }

    /**
     Checks if the model contains a unsupported os

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkOsType(Set<RootNode> nodes) {
        OsCheckNodeVisitor nodeVisitor = new OsCheckNodeVisitor(logger);
        for (RootNode node : nodes) {
            try {
                node.accept(nodeVisitor);
            } catch (UnsupportedOsTypeException e) {
                logger.warn(e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     Checks if there are any unsupported node types

     @param nodes - Nodes to be checked
     @return boolean - true if successful, false otherwise
     */
    private boolean checkNodeTypes(Set<RootNode> nodes) {
        for (RootNode node : nodes)
            try {
                node.accept(new NodeTypeCheckVisitor());
            } catch (UnsupportedOperationException e) {
                return false;
            }
        return true;
    }

    @Override
    public void prepare() {
        //throw new UnsupportedOperationException();
    }

    @Override
    public void transform() {
        Set<RootNode> nodes = model.getNodes();
        DockerApplicationVisitor dockerApplicationVisitor = new DockerApplicationVisitor();
        nodes.forEach(rootNode -> rootNode.accept(dockerApplicationVisitor));
        ResourceFileCreator resourceFileCreator
            = new ResourceFileCreator(Arrays.asList(dockerApplicationVisitor.getStack()));
        HashMap<String, String> result = null;
        try {
            result = resourceFileCreator.create();
        } catch (JsonProcessingException e) {
        }

        for (Map.Entry<String, String> entry : result.entrySet()) {
            String fileName = entry.getKey();
            String fileContent = entry.getValue();
            logger.info(fileContent);
            try {
                access.access("output/" + fileName + ".yaml").append(fileContent);
            } catch (IOException e) {

            }
        }
        //throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
        //throw new UnsupportedOperationException();
    }
}
