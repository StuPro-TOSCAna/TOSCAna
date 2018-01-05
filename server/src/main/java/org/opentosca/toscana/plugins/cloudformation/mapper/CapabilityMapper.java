package org.opentosca.toscana.plugins.cloudformation.mapper;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

public class CapabilityMapper {

    public static String mapOsCapabilityToImageId(OsCapability osCapability) {
        String imageId = "";
        //here should be a check for isPresent, but what to do if not present?
        if (osCapability.getType().get().equals(OsCapability.Type.LINUX) &&
            osCapability.getDistribution().get().equals(OsCapability.Distribution.UBUNTU) &&
            osCapability.getVersion().get().equals("16.04")) {
            imageId = "ami-0def3275";
        } else {
            throw new UnsupportedTypeException("Only Linux, Ubuntu 16.04 supported.");
        }
        return imageId;
    }

    public static String mapComputeCapabilityToInstanceType(ComputeCapability computeCapability) {
        String instanceType = "";
        //here should be a check for isPresent, but what to do if not present?
        if (computeCapability.getNumCpus().get().equals(1) &&
            computeCapability.getMemSizeInMB().get().equals(1024)) {
            instanceType = "t2.micro";
        } else {
            throw new UnsupportedTypeException("Only 1 CPU and 1024 MB memory supported.");
        }
        return instanceType;
    }
}
