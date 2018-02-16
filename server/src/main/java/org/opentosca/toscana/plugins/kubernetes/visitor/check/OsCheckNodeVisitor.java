package org.opentosca.toscana.plugins.kubernetes.visitor.check;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import org.slf4j.Logger;

/**
 Visitor to check the ComputeNode for unsupported OsCapabilities
 */
public class OsCheckNodeVisitor implements NodeVisitor {
    private final Logger logger;

    public OsCheckNodeVisitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void visit(Compute node) {
        checkOperatingSystem(node.getCapabilities());
    }

    private void checkOperatingSystem(Set<Capability> capabilities) {
        List<OsCapability.Type> unsupportedNodeTypes = Arrays.asList(OsCapability.Type.WINDOWS);
        OsCheckCapabilityVisitor visitor = new OsCheckCapabilityVisitor(unsupportedNodeTypes, logger);
        capabilities.forEach(capability -> capability.accept(visitor));
    }
}
