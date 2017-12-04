package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.Arrays;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.visitor.NodeVisitor;

public class OsCheckNodeVisitor implements NodeVisitor {
    @Override
    public void visit(Compute node) {
        checkOperatingSystem(node.getCapabilities());
    }

    private void checkOperatingSystem(Set<Capability> capabilities) {
        OsCheckCapabilityVisitor visitor = new OsCheckCapabilityVisitor();
        visitor.setUnsupportedTypes(Arrays.asList(OsCapability.Type.WINDOWS));
        capabilities.forEach(capability -> capability.accept(visitor));
    }
}
