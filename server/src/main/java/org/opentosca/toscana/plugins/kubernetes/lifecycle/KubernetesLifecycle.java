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

/**
 Implements the Lifecycle Operations for the Kubernetes plugin.
 <p>
 In order to keep the Filesize (in terms of LOC) small all phases have been put into
 separate classes (extending the LifecycleHandler. This class just stores the Fields that are commonly used in these
 phases.
 */
public final class KubernetesLifecycle extends AbstractLifecycle {

    /**
     The Effective Model (stores the TOSCA topology)
     */
    protected final EffectiveModel model;

    /**
     A Name to Node Map for every node in the topology (Nodes get wrapped in KubernetesNodeContainers)
     <p>
     This field gets populated with data during the <code>prepare()</code> phase
     */
    protected Map<String, KubernetesNodeContainer> nodes = new HashMap<>();
    /**
     A Set of all compute nodes in the topology
     <p>
     This field gets populated during the <code>prepare()</code> phase
     */
    protected Set<KubernetesNodeContainer> computeNodes = new HashSet<>();
    /**
     The set of nodeStacks (nodes that will belong into the same container image). the grouping process is done during the
     <code>prepare()</code> phase
     */
    protected Set<NodeStack> stacks = new HashSet<>();
    /**
     The list of Pods.
     <p>
     This list gets created at the end of the <code>prepare()</code> phase (after all NodeStacks have been created)
     Some of the data will be populated during the transform phase (for example: Open ports)
     Doing this so early is needed to know the Public and Private addresses for a Compute node
     (In kubernetes this is represented as as Service)
     */
    protected List<Pod> pods = null;

    //Lifecycle phase implementations
    //Check Phases
    private CheckHandler checkHandler;
    //Prepare Phase
    private PrepareHandler prepareHandler;
    //Transform and Cleanup Phase
    private TransformHandler transformHandler;

    public KubernetesLifecycle(TransformationContext context, BaseImageMapper mapper) throws IOException {
        super(context);
        model = context.getModel();

        boolean pushToRegistry;
        //Set push to registry to false if the inputs are null (only relevant for some tests)
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
