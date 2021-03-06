package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationLifecycle;
import org.opentosca.toscana.plugins.cloudformation.CloudFormationModule;

import com.scaleset.cfbuilder.core.Fn;
import com.scaleset.cfbuilder.core.Template;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.URL_HTTP;

/**
 Utility class for working with CloudFormation stacks.
 */
public class StackUtils {

    private static final String USERDATA_NAME = "Join";
    private static final String USERDATA_DELIMITER = "";
    private static final String[] USERDATA_CONSTANT_PARAMS = {
        "#!/bin/bash -xe\n",
        "mkdir -p /tmp/aws-cfn-bootstrap-latest\n",
        "curl https://s3.amazonaws.com/cloudformation-examples/aws-cfn-bootstrap-latest.tar.gz | tar xz -C " +
            "/tmp/aws-cfn-bootstrap-latest --strip-components 1\n",
        "apt-get update\n",
        "DEBIAN_FRONTEND=noninteractive apt-get upgrade -yq\n",
        "apt-get -y install python-setuptools\n",
        "easy_install /tmp/aws-cfn-bootstrap-latest\n",
        "cp /tmp/aws-cfn-bootstrap-latest/init/ubuntu/cfn-hup /etc/init.d/cfn-hup\n",
        "chmod 755 /etc/init.d/cfn-hup\n",
        "update-rc.d cfn-hup defaults\n",
        "# Install the files and packages from the metadata\n",
        "/usr/local/bin/cfn-init -v ",
        "         --stack "};

    /**
     Returns a random DNS-compliant bucket name.

     @return random bucket name
     */
    public static String getRandomBucketName() {
        return "toscana-bucket-" + UUID.randomUUID();
    }

    /**
     Returns a random DNS-compliant stack name.

     @return random stack name
     */
    public static String getRandomStackName() {
        return "toscana-stack-" + UUID.randomUUID();
    }

    /**
     Returns the FN userdata for the given resource and configsets.

     @param resource   to which this userdata belongs
     @param configsets name of the configsets for the resource
     @param cfnModule  belonging to the template
     @return Fn userdata for a resource
     */
    public static Fn getUserDataFn(String resource, String configsets, CloudFormationModule cfnModule) {
        Template template = cfnModule.getTemplate();
        // Initialise params that need refs
        Object[] userdataRefParams = {
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --configsets " + configsets + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n",
            "# Signal the status from cfn-init\n",
            "/usr/local/bin/cfn-signal -e $? ",
            "         --stack ",
            template.ref("AWS::StackName"),
            "         --resource " + resource + " ",
            "         --region ",
            template.ref("AWS::Region"),
            "\n"};

        // Combine constant params with ref params
        List<Object> params = new ArrayList<>();
        Collections.addAll(params, USERDATA_CONSTANT_PARAMS);
        Collections.addAll(params, userdataRefParams);

        return Fn.fnDelimiter(USERDATA_NAME, USERDATA_DELIMITER, params.toArray());
    }

    /**
     Returns a userdata {@link Fn} that installs mysql-client, executes the {@code sql} query on the
     {@link MysqlDatabase} and shuts down the machine afterwards.

     @param mysqlDatabase the {@link MysqlDatabase} the EC2 this {@link Fn} is for will connect to
     @param sql           the sql query that should be executed
     @return returns a {@link Fn} that a EC2 can set as userdata
     */
    public static Fn getUserDataDBConnFn(MysqlDatabase mysqlDatabase, String sql) {
        String dbName = mysqlDatabase.getDatabaseName();
        String user = mysqlDatabase.getUser().orElseThrow(() -> new IllegalArgumentException("Database user not set"));
        String password = mysqlDatabase.getPassword().orElseThrow(() -> new IllegalArgumentException("Database " +
            "password not set"));
        Integer port = mysqlDatabase.getPort().orElseThrow(() -> new IllegalArgumentException("Database port not set"));
        Object[] userdata = {
            "#!/bin/bash -xe\n",
            "apt-get update\n",
            "#DEBIAN_FRONTEND=noninteractive apt-get upgrade -yq\n",
            "apt-get -y install mysql-client\n",
            "mysql --user=\"",
            user,
            "\" --password=\"",
            password,
            "\" --port=",
            port,
            " --host=",
            Fn.fnGetAtt(CloudFormationLifecycle.toAlphanumerical(mysqlDatabase.getEntityName()), "Endpoint.Address"),
            " -e \"",
            sql,
            "\" ",
            dbName,
            "\n",
            "sudo shutdown now\n"
        };
        return Fn.fnDelimiter(USERDATA_NAME, USERDATA_DELIMITER, userdata);
    }

    /**
     Returns the URL to the file in the given S3Bucket.
     e.g. http://bucketName.s3.amazonaws.com/objectKey

     @param bucketName name of the bucket containing the file
     @param objectKey  key belonging to the file in the bucket
     @return URL for the file
     */
    public static String getFileURL(String bucketName, String objectKey) {
        return URL_HTTP + bucketName + CloudFormationModule.URL_S3_AMAZONAWS + "/" + objectKey;
    }
}
