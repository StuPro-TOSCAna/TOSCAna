package org.opentosca.toscana.core.util;

import java.util.List;

import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;

public interface LifecycleAccess {
    List<? extends LifecyclePhase> getLifecyclePhases();
}
