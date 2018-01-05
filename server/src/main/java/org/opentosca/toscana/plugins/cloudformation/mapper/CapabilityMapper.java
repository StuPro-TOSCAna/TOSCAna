package org.opentosca.toscana.plugins.cloudformation.mapper;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityMapper {

    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapper.class);

    private static final ImmutableMap<MultiKey, String> INSTANCE_TYPES = ImmutableMap.<MultiKey, String>builder()
        .put(new MultiKey(1, 512) , "t2.nano")
        .put(new MultiKey(1, 1024), "t2.micro")
        .put(new MultiKey(1, 2048) , "t2.small")
        .put(new MultiKey(2, 4096) , "t2.medium")
        .put(new MultiKey(2, 8192) , "t2.large")
        .put(new MultiKey(4, 16384) , "t2.xlarge")
        .put(new MultiKey(8, 32768) , "t2.2xlarge")
        .build();

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
        //TODO what to do with disksize?
        //default type is t2.micro
        String instanceType;
        if (computeCapability.getNumCpus().isPresent() &&
            computeCapability.getMemSizeInMB().isPresent()) {
            // if numcpu not key1 or mem not key2 scale upwards!
            instanceType = INSTANCE_TYPES.get(new MultiKey(computeCapability.getNumCpus().get(), computeCapability
                .getMemSizeInMB().get()));
            if (instanceType == null) {
                throw new UnsupportedTypeException("Combination of NumCpus: " + computeCapability.getNumCpus().get() +
                    " and Memory: " + computeCapability.getMemSizeInMB().get() + " is not available");
            }
        } else {
            logger.warn("NumCpus and MemSize not both given, defaulting to t2.micro");
            instanceType = "t2.micro";
        }
        return instanceType;
    }
}
