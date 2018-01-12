package org.opentosca.toscana.plugins.cloudformation.mapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityMapper {

    private final static Logger logger = LoggerFactory.getLogger(CapabilityMapper.class);

    private static final ImmutableMap<MultiKey, String> INSTANCE_TYPES = ImmutableMap.<MultiKey, String>builder()
        .put(new MultiKey(1, 512), "t2.nano")
        .put(new MultiKey(1, 1024), "t2.micro")
        .put(new MultiKey(1, 2048), "t2.small")
        .put(new MultiKey(2, 4096), "t2.medium")
        .put(new MultiKey(2, 8192), "t2.large")
        .put(new MultiKey(4, 16384), "t2.xlarge")
        .put(new MultiKey(8, 32768), "t2.2xlarge")
        .build();

    //need to be sorted !!
    private static ImmutableList<Integer> CPUS = ImmutableList.<Integer>builder()
        .add(1)
        .add(2)
        .add(4)
        .add(8)
        .build();

    public static String mapOsCapabilityToImageId(OsCapability osCapability) {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
            .withRegion(Regions.US_WEST_2) //TODO get this from user
            .build();
        //need to set these, owner are self and amazon
        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
            .withFilters(
                new Filter("virtualization-type").withValues("hvm"),
                new Filter("root-device-type").withValues("ebs"))
            .withOwners("099720109477");
        //TODO set filter  with owners, executable users?
        if (osCapability.getType().isPresent()) {
            if (osCapability.getType().get().equals(OsCapability.Type.WINDOWS)) {
                describeImagesRequest.withFilters(new Filter("platform").withValues("windows"));
            }
        }
        if (osCapability.getDistribution().isPresent()) {
            if (osCapability.getDistribution().get().equals(OsCapability.Distribution.UBUNTU)) {
                // /ubuntu/images/ gets better results
                describeImagesRequest.withFilters(new Filter("name").withValues("*ubuntu/images/*"));
            } else {
                //just search for the string
                describeImagesRequest.withFilters(new Filter("name").withValues("*" + osCapability.getDistribution().toString() + "*"));
            }
        }
        if (osCapability.getVersion().isPresent()) {
            describeImagesRequest.withFilters(new Filter("name").withValues("*" + osCapability.getVersion().get() + "*"));
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
        String imageId = "";
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
        }
        return imageId;
    }

    public static String mapComputeCapabilityToInstanceType(ComputeCapability computeCapability) {
        //TODO what to do with disksize?
        //default type is t2.micro
        String instanceType;
        if (computeCapability.getNumCpus().isPresent() &&
            computeCapability.getMemSizeInMB().isPresent()) {
            Integer numCpus = computeCapability.getNumCpus().get();
            Integer memSize = computeCapability.getMemSizeInMB().get();
            // if numcpu not key1 or mem not key2 scale upwards!
            if (!CPUS.contains(numCpus)) {
                for (Integer num : CPUS) {
                    if (num > numCpus) {
                        numCpus = num;
                        break;
                    }
                }
            }
            instanceType = INSTANCE_TYPES.get(new MultiKey(numCpus, memSize));
            if (instanceType == null) {
                throw new UnsupportedTypeException("Combination of NumCpus: " + numCpus +
                    " and Memory: " + computeCapability.getMemSizeInMB().get() + " is not available");
            }
        } else {
            logger.warn("NumCpus and MemSize not both given, defaulting to t2.micro");
            instanceType = "t2.micro";
        }
        return instanceType;
    }
}
