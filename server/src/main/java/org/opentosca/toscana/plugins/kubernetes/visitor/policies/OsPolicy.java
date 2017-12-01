package org.opentosca.toscana.plugins.kubernetes.visitor.policies;

import java.util.List;

import org.opentosca.toscana.model.capability.OsCapability;

public class OsPolicy implements CapabilityPolicy {
    private List<OsCapability.Type> unsupportedTypes;

    public OsPolicy(List<OsCapability.Type> unsupportedTypes) {
        this.unsupportedTypes = unsupportedTypes;
    }

    public List<OsCapability.Type> getUnsupportedTypes() {
        return this.unsupportedTypes;
    }
}
