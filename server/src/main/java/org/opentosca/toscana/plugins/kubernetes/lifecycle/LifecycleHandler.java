package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import org.slf4j.Logger;

/**
 Simple Parent class for the externalized phases in the Kubernetes Lifecycle
 <p>
 This class just stores the Lifecycle instance and
 creates a new Logger for the Class (Using getClass() to ensure its for the right class)
 */
abstract class LifecycleHandler {
    protected KubernetesLifecycle lifecycle;
    protected Logger logger;

    LifecycleHandler(KubernetesLifecycle lifecycle) {
        this.lifecycle = lifecycle;
        logger = lifecycle.getContext().getLogger(getClass());
    }
}
