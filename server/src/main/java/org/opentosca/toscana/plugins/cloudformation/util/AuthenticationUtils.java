package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.cloudformation.Authentication;
import com.scaleset.cfbuilder.iam.InstanceProfile;
import com.scaleset.cfbuilder.iam.Policy;
import com.scaleset.cfbuilder.iam.PolicyDocument;
import com.scaleset.cfbuilder.iam.Principal;
import com.scaleset.cfbuilder.iam.Role;
import com.scaleset.cfbuilder.iam.Statement;

import static com.scaleset.cfbuilder.core.Ref.ref;

/**
 Class for building IAM resources and authentications to access S3 buckets.
 */
public class AuthenticationUtils {

    public static final String INSTANCE_PROFILE = "InstanceProfile";
    
    private static final String INSTANCE_ROLE = "InstanceRole";
    private static final String ARN_AWS_S3 = "arn:aws:s3:::";
    private static final String EC2_AMAZONAWS_COM = "ec2.amazonaws.com";
    
    /**
     Returns an <tt>Authentication<tt> to access the given S3Bucket.

     @return authentication for S3
     */
    public static Authentication getS3Authentication(String bucketName) {
        return new Authentication("S3Creds")
            .addBucket(bucketName)
            .roleName(ref(INSTANCE_ROLE))
            .type("S3");
    }

    /**
     Returns the <tt>Policy<tt> to access S3 for the given module.
     Note: Roles must still be set.

     @return policy to access S3
     */
    public static Policy getS3Policy(CloudFormationModule cfnModule) {
        Statement statement = new Statement().addAction("s3:GetObject").effect("Allow")
            .addResource(ARN_AWS_S3 + cfnModule.getBucketName() + "/*");
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return cfnModule.resource(Policy.class, "RolePolicies")
            .policyName("S3Download")
            .policyDocument(policyDocument);
    }

    /**
     Returns the <tt>Role<tt> to access S3 for the given module.

     @return Role to access S3
     */
    public static Role getS3InstanceRole(CloudFormationModule cfnModule) {
        List<String> resourceList = new ArrayList<>();
        resourceList.add(EC2_AMAZONAWS_COM);
        Principal principal = new Principal().principal("Service", resourceList);
        Statement statement = new Statement().addAction("sts:AssumeRole").effect("Allow").principal(principal);
        PolicyDocument policyDocument = new PolicyDocument().addStatement(statement);
        return cfnModule.resource(Role.class, INSTANCE_ROLE)
            .path("/")
            .assumeRolePolicyDocument(policyDocument);
    }

    /**
     Returns the <tt>Instanceprofile<tt> to access S3 for the given module.
     Note: Roles must still be set.

     @return instanceProfile to access S3
     */
    public static InstanceProfile getS3InstanceProfile(CloudFormationModule cfnModule) {
        return cfnModule.resource(InstanceProfile.class, INSTANCE_PROFILE)
            .path("/");
    }
}
