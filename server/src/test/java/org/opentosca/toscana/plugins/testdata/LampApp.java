package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.core.transformation.properties.PropertyType;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ContainerCapability.ContainerCapabilityBuilder;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.MysqlDbmsRequirement;
import org.opentosca.toscana.model.requirement.WebServerRequirement;

public class LampApp {

    public static EffectiveModel getLampApp() {
        return new EffectiveModel(createLampNodes(), createLampInputs());
    }

    private static Set<Property> createLampInputs() {
        Set<Property> inputs = new HashSet<>();
        inputs.add(new Property("database_name", PropertyType.TEXT));
        inputs.add(new Property("database_port", PropertyType.INTEGER));
        inputs.add(new Property("database_password", PropertyType.TEXT));
        inputs.add(new Property("database_user", PropertyType.TEXT));

        return inputs;
    }

    private static Set<RootNode> createLampNodes() {
        Set<RootNode> testNodes = new HashSet<>();
        Compute compute = createComputeNode();
        Apache webserver = createApache(compute);
        MysqlDbms dbms = createMysqlDbms(compute);
        MysqlDatabase database = createMysqlDatabase(dbms);
        WebApplication webApplication = createWebApplication(webserver, database);

        testNodes.add(compute);
        testNodes.add(webserver);
        testNodes.add(dbms);
        testNodes.add(database);
        testNodes.add(webApplication);
        return testNodes;
    }

    private static Compute createComputeNode() {
        OsCapability osCapability = OsCapability
            .builder()
            .distribution(OsCapability.Distribution.UBUNTU)
            .type(OsCapability.Type.LINUX)
            .version("16.04")
            .build();
        Compute computeNode = Compute
            .builder("server")
            .os(osCapability)
            .host(createContainerCapability())
            .build();
        return computeNode;
    }

    private static ContainerCapability createContainerCapability() {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        validSourceTypes.add(MysqlDbms.class);

        ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(1024)
            .diskSizeInMB(2000)
            .numCpus(1)
            .validSourceTypes(validSourceTypes);

        return containerCapabilityBuilder.build();
    }

    private static MysqlDbms createMysqlDbms(Compute compute) {
        return MysqlDbms.builder(
            "mysql_dbms",
            "geheim12")
            .host(HostRequirement.builder().fulfiller(compute).build()) //TODO Relationship?
            .port(3306)
            .build();
    }

    private static MysqlDatabase createMysqlDatabase(MysqlDbms dbms) {
        Operation databaseConfigureOperation = Operation.builder()
            .artifact(Artifact.builder("artifact", "my_db/createtable.sql").build())
            .build();

        StandardLifecycle lifecycle = StandardLifecycle.builder()
            .configure(databaseConfigureOperation)
            .build();
        MysqlDatabase mydb = MysqlDatabase
            .builder("my_db", "DBNAME")
            .user("root")
            .password("geheim12")
            .port(3306)
            .standardLifecycle(lifecycle)
            .mysqlHost(MysqlDbmsRequirement.builder().fulfiller(dbms).build()) //TODO Relationship?
            .build();

        return mydb;
    }

    private static Apache createApache(Compute compute) {
        ContainerCapability containerCapability = createContainerCapability();
        Operation apacheConfigureOperation = Operation.builder()
            .artifact(Artifact.builder("artifact", "my_apache/install_php.sh").build())
            .build();
        StandardLifecycle lifecycle = StandardLifecycle.builder()
            .configure(apacheConfigureOperation)
            .build();
        return Apache
            .builder("apache_web_server")
            .containerHost(containerCapability)
            .host(getHostedOnServerRequirement(compute))
            .lifecycle(lifecycle)
            .build();
    }

    private static WebApplication createWebApplication(Apache webserver, MysqlDatabase database) {
        Set<String> appDependencies = new HashSet<>();
        appDependencies.add("my_app/myphpapp.php");
        appDependencies.add("my_app/mysql-credentials.php");
        Operation appCreate = Operation.builder()
            .artifact(Artifact.builder("artifact", "my_app/create_myphpapp.sh").build())
            .dependencies(appDependencies)
            .build();

        Set<OperationVariable> appInputs = new HashSet<>();
        appInputs.add(new OperationVariable("database_host", "127.0.0.1")); //TODO what to put in here?
        appInputs.add(new OperationVariable("database_password", database.getPassword().get()));
        appInputs.add(new OperationVariable("database_name", database.getDatabaseName()));
        appInputs.add(new OperationVariable("database_user", database.getUser().get()));
        appInputs.add(new OperationVariable("database_port", database.getPort().get().toString()));

        Operation appConfigure = Operation.builder()
            .artifact(Artifact.builder("artifact", "my_app/configure_myphpapp.sh").build())
            .inputs(appInputs)
            .build();

        StandardLifecycle webAppLifecycle = StandardLifecycle.builder()
            .create(appCreate)
            .configure(appConfigure)
            .build();

        WebApplication webApplication = WebApplication
            .builder("my_app")
            .host(WebServerRequirement.builder().fulfiller(webserver).build()) //TODO Relationship
            .standardLifecycle(webAppLifecycle)
            .build();

        return webApplication;
    }

    private static HostRequirement getHostedOnServerRequirement(Compute compute) {
        ContainerCapability hostCapability = ContainerCapability.builder().resourceName("server").build();

        return HostRequirement.builder()
            .capability(hostCapability).fulfiller(compute).build();
    }
}
