package org.opentosca.toscana.plugins.cloudfoundry.visitor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;

import org.slf4j.Logger;

/**
 Created by jensmuller on 09.02.18.
 */
public class OSCheck implements NodeVisitor {

    private final Logger logger;
    private final List<OsCapability.Type> unsupportedOStypes = Arrays.asList(OsCapability.Type.WINDOWS,
        OsCapability.Type.MAC);

    public OSCheck(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void visit(Compute node) {
        logger.debug("Checking OS types");
        Set<Capability> capabilities = node.getCapabilities();
        OSCapCheck osCapCheck = new OSCapCheck();
        capabilities.forEach(capability -> capability.accept(osCapCheck));
        logger.debug("OS Type Check was successful");
    }

    private class OSCapCheck implements CapabilityVisitor {
        @Override
        public void visit(OsCapability capability) {
            Optional<OsCapability.Type> type = capability.getType();
            if (type.isPresent() && unsupportedOStypes.contains(type)) {
                logger.error("Found unsupported OS type");
                throw new UnsupportedOsTypeException(type.get());
            }
        }
    }
}
