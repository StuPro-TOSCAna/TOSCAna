package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import org.slf4j.Logger;

abstract class LifecycleHandler {
    protected KubernetesLifecycle lifecycle;
    protected Logger logger;

    LifecycleHandler(KubernetesLifecycle lifecycle) {
        this.lifecycle = lifecycle;
        logger = lifecycle.getContext().getLogger(getClass());
    }
}
