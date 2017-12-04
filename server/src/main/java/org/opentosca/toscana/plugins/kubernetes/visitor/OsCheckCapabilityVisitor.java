package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.List;

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
        logger.debug("Checking the operating system type.");
        OsCapability.Type capabilityType = capability.getType().get();
        if (unsupportedTypes.contains(capabilityType)) {
            throw new UnsupportedOsTypeException(OsCapability.class);
        } else {
            logger.debug("Checking was successful.");
        }
    }
}
