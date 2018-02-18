package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.plugins.kubernetes.KubernetesPlugin;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.model.transform.Pod;
import org.opentosca.toscana.plugins.kubernetes.util.KubernetesNodeContainer;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;

public class KubernetesLifecycle extends AbstractLifecycle {
    
    final EffectiveModel model;

    Map<String, KubernetesNodeContainer> nodes = new HashMap<>();
    Set<KubernetesNodeContainer> computeNodes = new HashSet<>();
    Set<NodeStack> stacks = new HashSet<>();
    List<Pod> pods = null;
    
    private CheckHandler checkHandler;
    private PrepareHandler prepareHandler;
    private TransformHandler transformHandler;

    public KubernetesLifecycle(TransformationContext context, BaseImageMapper mapper) throws IOException {
        super(context);
        model = context.getModel();
        //Fix failing K8s plugin test
        boolean pushToRegistry;
        if (context.getInputs() == null) {
            pushToRegistry = false;
        } else {
            pushToRegistry = Boolean.parseBoolean(
                context.getInputs().getOrThrow(KubernetesPlugin.DOCKER_PUSH_TO_REGISTRY_PROPERTY_KEY)
            );
        }
        
        // Initialize Handlers
        this.checkHandler = new CheckHandler(this);
        this.prepareHandler = new PrepareHandler(this);
        this.transformHandler = new TransformHandler(this, mapper, pushToRegistry);
    }

    @Override
    public boolean checkModel() {
        return checkHandler.checkModel();
    }

    @Override
    public void prepare() {
        prepareHandler.prepare();
    }

    @Override
    public void transform() {
        transformHandler.transform();
    }

    @Override
    public void cleanup() {
        transformHandler.cleanup();
    }
}
