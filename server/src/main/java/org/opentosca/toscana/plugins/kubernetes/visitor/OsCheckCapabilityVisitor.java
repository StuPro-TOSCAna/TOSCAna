package org.opentosca.toscana.plugins.kubernetes.visitor;

import java.util.List;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;
import org.opentosca.toscana.plugins.kubernetes.exceptions.UnsupportedOsTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsCheckCapabilityVisitor implements CapabilityVisitor {
    private final static Logger logger = LoggerFactory.getLogger(OsCheckCapabilityVisitor.class.getName());
    private List<OsCapability.Type> unsupportedTypes;

    @Override
    public void visit(OsCapability capability) {
        logger.info("Visiting the OS capability.");
        OsCapability.Type capabilityType = capability.getType().get();
        if (unsupportedTypes.contains(capabilityType)) {
            throw new UnsupportedOsTypeException(OsCapability.class);
        } else {
            logger.info("Everything ok");
        }
    }

    public void setUnsupportedTypes(List<OsCapability.Type> unsupportedTypes) {
        this.unsupportedTypes = unsupportedTypes;
    }
}
