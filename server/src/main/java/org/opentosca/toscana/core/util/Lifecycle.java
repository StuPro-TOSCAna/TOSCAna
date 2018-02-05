package org.opentosca.toscana.core.util;

import java.util.List;

public interface Lifecycle {

    List<? extends LifecyclePhase> getLifecyclePhases();
}
