package org.opentosca.toscana.plugins.cloudformation.mapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityMapper {

    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapper.class);

    private final ImmutableList<InstanceType> EC2_INSTANCE_TYPES = ImmutableList.<InstanceType>builder()
        .add(new InstanceType("t2.nano", 1, 512))
        .add(new InstanceType("t2.micro", 1, 1024))
        .add(new InstanceType("t2.small", 1, 2048))
        .add(new InstanceType("t2.medium", 2, 4096))
        .add(new InstanceType("t2.large", 2, 8192))
        .add(new InstanceType("t2.xlarge", 4, 16384))
        .add(new InstanceType("t2.2xlarge", 8, 32768))
        .build();

    private final ImmutableList<InstanceType> RDS_INSTANCE_CLASSES = ImmutableList.<InstanceType>builder()
        .add(new InstanceType("db.t2.micro", 1, 1024))
        .add(new InstanceType("db.t2.small", 1, 2048))
        .add(new InstanceType("db.t2.medium", 2, 4096))
        .add(new InstanceType("db.t2.large", 2, 8192))
        .add(new InstanceType("db.t2.xlarge", 4, 16384))
        .add(new InstanceType("db.t2.2xlarge", 8, 32768))
        .add(new InstanceType("db.m4.4xlarge", 16, 65536))
        .add(new InstanceType("db.m4.10xlarge", 40, 163840))
        .add(new InstanceType("db.m4.16xlarge", 64, 262144))
        .build();

    public String mapOsCapabilityToImageId(OsCapability osCapability) {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
            .withRegion(Regions.US_WEST_2) //TODO get this from user
            .build();
        //need to set these, owner are self and amazon
        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
            .withFilters(
                new Filter("virtualization-type").withValues("hvm"),
                new Filter("root-device-type").withValues("ebs"))
            .withOwners("099720109477");
        if (osCapability.getType().isPresent()) {
            if (osCapability.getType().get().equals(OsCapability.Type.WINDOWS)) {
                describeImagesRequest.withFilters(new Filter("platform").withValues("windows"));
            }
        }
        if (osCapability.getDistribution().isPresent()) {
            if (osCapability.getDistribution().get().equals(OsCapability.Distribution.UBUNTU)) {
                // */ubuntu/images/* gets better results than plain *ubuntu*
                describeImagesRequest.withFilters(new Filter("name").withValues("*ubuntu/images/*"));
            } else {
                //just search for the string
                describeImagesRequest.withFilters(new Filter("name").withValues("*" + osCapability.getDistribution()
                    .toString() + "*"));
            }
        }
        if (osCapability.getVersion().isPresent()) {
            describeImagesRequest.withFilters(new Filter("name").withValues("*" + osCapability.getVersion().get() +
                "*"));
        }
        if (osCapability.getArchitecture().isPresent()) {
            if (osCapability.getArchitecture().get().equals(OsCapability.Architecture.x86_64)) {
                describeImagesRequest.withFilters(new Filter("architecture").withValues("x86_64"));
            } else if (osCapability.getArchitecture().get().equals(OsCapability.Architecture.x86_32)) {
                describeImagesRequest.withFilters(new Filter("architecture").withValues("i386"));
            }
        }
        DescribeImagesResult describeImagesResult = ec2.describeImages(describeImagesRequest);
        Integer numReceivedImages = describeImagesResult.getImages().size();
        logger.debug("Got " + numReceivedImages + " images from aws");
        String imageId;
        if (numReceivedImages > 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Map<Date, Image> creationDateMap = new HashMap<>();
            for (Image image : describeImagesResult.getImages()) {
                try {
                    Date date = dateFormat.parse(image.getCreationDate());
                    creationDateMap.put(date, image);
                } catch (ParseException pE) {
                    logger.error("Error parsing dateformat");
                    pE.printStackTrace();
                }
            }
            Image latest = creationDateMap.get(Collections.max(creationDateMap.keySet()));
            logger.debug("Latest image received: " + latest.toString());
            imageId = latest.getImageId();
        } else {
            logger.warn("No images received defaulting to old ubuntu 16.04 image");
            imageId = "ami-0def3275";
            //TODO maybe not defaulting but throwing a transformation failed exception?
        }
        logger.debug("ImageId is: " + imageId);
        return imageId;
    }

    public String mapComputeCapabilityToInstanceType(ComputeCapability computeCapability, String distinction) throws TransformationFailureException {
        //TODO what to do with disksize?
        Integer numCpus = computeCapability.getNumCpus().orElse(0);
        Integer memSize = computeCapability.getMemSizeInMB().orElse(0);
        //default type the smallest
        final ImmutableList<InstanceType> instanceTypes;
        if ("EC2".equals(distinction)) {
            instanceTypes = EC2_INSTANCE_TYPES;
        } else if ("RDS".equals(distinction)) {
            instanceTypes = RDS_INSTANCE_CLASSES;
        } else {
            throw new IllegalArgumentException("Distinguisher not supported");
        }
        List<Integer> allNumCpus = instanceTypes.stream()
            .map(InstanceType::getNumCpus)
            .sorted()
            .collect(Collectors.toList());
        List<Integer> allMemSizes = instanceTypes.stream()
            .map(InstanceType::getMemSize)
            .sorted()
            .collect(Collectors.toList());
        // if numcpu not key1 or mem not key2 scale upwards!
        try {
            logger.debug("Check numCpus: " + numCpus);
            numCpus = checkValue(numCpus, allNumCpus);
            logger.debug("Check memSize: " + memSize);
            memSize = checkValue(memSize, allMemSizes);
        } catch (IllegalArgumentException ie) {
            String errorMessage = "Values numCpus: " + numCpus + " and/or memSize: " + memSize + " are to big. No " +
                "InstanceType found";
            logger.error(errorMessage);
            throw new TransformationFailureException(errorMessage, ie);
        }
        //get instanceType from combination
        String instanceType = findCombination(numCpus, memSize, instanceTypes, allNumCpus, allMemSizes);
        logger.debug("InstanceType is: " + instanceType);
        return instanceType;
    }

    public Integer mapComputeCapabilityToAllocatedStorage(ComputeCapability computeCapability) {
        final Integer minSize = 20;
        final Integer maxSize = 6144;
        Integer diskSize = computeCapability.getDiskSizeInMB().orElse(minSize * 1000);
        diskSize = diskSize / 1000;
        if (diskSize > maxSize) {
            logger.debug("Disk size: " + maxSize);
            return maxSize;
        }
        if (diskSize < minSize) {
            logger.debug("Disk size: " + minSize);
            return minSize;
        }
        logger.debug("Disk size: " + diskSize);
        return diskSize;
    }

    private Integer checkValue(Integer value, List<Integer> checker) throws IllegalArgumentException {
        if (!checker.contains(value)) {
            for (Integer num : checker) {
                if (num > value) {
                    return num;
                }
            }
            throw new IllegalArgumentException("Can't support value: " + value);
        } else {
            return value;
        }
    }

    private String findCombination(Integer numCpus, Integer memSize, ImmutableList<InstanceType> instanceTypes, List<Integer> allNumCpus, List<Integer> allMemSizes) {
        String instanceType = getInstanceType(numCpus, memSize, instanceTypes);
        if ("".equals(instanceType)) {
            //the combination does not exist
            //try to scale cpu
            logger.debug("The combination of numCpus: " + numCpus + " and memSize: " + memSize + " does not exist");
            logger.debug("Try to scale cpu");
            for (Integer num : allNumCpus) {
                if (num > numCpus) {
                    if (getMemByCpu(num, instanceTypes).contains(memSize)) {
                        numCpus = num;
                        break;
                    }
                }
            }
            instanceType = getInstanceType(numCpus, memSize, instanceTypes);
            if ("".equals(instanceType)) {
                logger.debug("Scaling cpu failed");
                logger.debug("Try to scale memory");
                //try to scale mem
                for (Integer mem : allMemSizes) {
                    if (mem > memSize) {
                        if (getCpuByMem(mem, instanceTypes).contains(numCpus)) {
                            memSize = mem;
                            break;
                        }
                    }
                }
                instanceType = getInstanceType(numCpus, memSize, instanceTypes);
                if ("".equals(instanceType)) {
                    throw new TransformationFailureException("No combination of numCpus and memSize found");
                } else {
                    logger.debug("Scaling memSize succeeded, memSize: " + memSize);
                }
            } else {
                logger.debug("Scaling numCpus succeeded, numCpus: " + numCpus);
            }
        }
        return instanceType;
    }

    private List<Integer> getMemByCpu(Integer numCpus, ImmutableList<InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus))
            .map(InstanceType::getMemSize)
            .collect(Collectors.toList());
    }

    private List<Integer> getCpuByMem(Integer memSize, ImmutableList<InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getMemSize().equals(memSize))
            .map(InstanceType::getNumCpus)
            .collect(Collectors.toList());
    }

    private String getInstanceType(Integer numCpus, Integer memSize, ImmutableList<InstanceType> instanceTypes) {
        Optional<InstanceType> instanceType = instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus) && u.getMemSize().equals(memSize))
            .findAny();
        if (instanceType.isPresent()) {
            return instanceType.get().getType();
        } else {
            return "";
        }
    }

    private class InstanceType {
        private String type;
        private Integer memSize;
        private Integer numCpus;

        protected InstanceType(String type, Integer numCpus, Integer memSize) {
            this.type = type;
            this.numCpus = numCpus;
            this.memSize = memSize;
        }

        protected String getType() {
            return type;
        }

        protected Integer getMemSize() {
            return memSize;
        }

        protected Integer getNumCpus() {
            return numCpus;
        }
    }
}
