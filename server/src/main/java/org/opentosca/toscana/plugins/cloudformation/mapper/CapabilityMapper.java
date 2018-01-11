package org.opentosca.toscana.plugins.cloudformation.mapper;

import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
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

    public static String mapOsCapabilityToImageId(BasicAWSCredentials awsCreds, OsCapability osCapability) {
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
            .withRegion(Regions.US_WEST_2)
            .build();
        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest()
            .withFilters(new Filter("name")
                .withValues("ubuntu", "linux", "16.04"));
        //TODO set filter  with owners, executable users?
        DescribeImagesResult describeImagesResult = ec2.describeImages(describeImagesRequest);
        for (Image image : describeImagesResult.getImages()){
            System.out.println(image.toString());
        }
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
