package org.opentosca.toscana.plugins.kubernetes.visitor.check;

import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;

import org.slf4j.Logger;

/**
 Visitor to check if there is a OsCapability with a unsupported OsType
 */
public class OsCheckCapabilityVisitor implements CapabilityVisitor {
    private final Logger logger;
    private final List<OsCapability.Type> unsupportedTypes;

    public OsCheckCapabilityVisitor(List<OsCapability.Type> unsupportedTypes, Logger logger) {
        this.unsupportedTypes = unsupportedTypes;
        this.logger = logger;
    }

    @Override
    public void visit(OsCapability capability) {
        logger.debug("Running the OsTypeCheck");
        Optional<OsCapability.Type> capabilityType = capability.getType();
        if (capability.getType().isPresent()) {
            if (unsupportedTypes.contains(capabilityType.get())) {
                throw new UnsupportedOsTypeException(capabilityType.get());
            } else {
                logger.debug("OsTypeCheck was successful");
            }
        }
    }
}
