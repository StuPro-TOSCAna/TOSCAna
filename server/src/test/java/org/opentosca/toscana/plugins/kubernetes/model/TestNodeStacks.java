package org.opentosca.toscana.plugins.kubernetes.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.testdata.KubernetesLampApp;

import com.google.common.collect.Sets;

public class TestNodeStacks {
    private HashSet<NodeStack> createNodeStack() {
        Map<String, RootNode> map = KubernetesLampApp.getLampModel();

        List<KubernetesNodeContainer> webAppNodes = new LinkedList<>();
        KubernetesNodeContainer computeContainer = new KubernetesNodeContainer(map.get("server"));
        computeContainer.hasParentComputeNode();
        webAppNodes.add(new KubernetesNodeContainer(map.get("my-app")));
        webAppNodes.add(new KubernetesNodeContainer(map.get("apache-web-server")));
        webAppNodes.add(computeContainer);

        NodeStack webAppNodeStack = new NodeStack(webAppNodes);
        return Sets.newHashSet(webAppNodeStack);
    }

    public static Set<NodeStack> getNodeStacks() {
        return new TestNodeStacks().createNodeStack();
    }
}
