package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.ContainerCapability.ContainerCapabilityBuilder;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.AttachesTo;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.MysqlDbmsRequirement;
import org.opentosca.toscana.model.requirement.WebServerRequirement;

public class LampApp {

    private final Set<RootNode> testNodes = new HashSet<>();

    public Set<RootNode> getLampApp() {
        createLampModel();
        return testNodes;
    }

    public static Set<RootNode> getLampModel() {
        return new LampApp().getLampApp();
    }

    private void createLampModel() {

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
    }

    private Compute createComputeNode() {
        AdminEndpointCapability computeAdminEndpointCap = AdminEndpointCapability
            .builder("127.0.0.1", new Port(80))
            .build();
        AttachesTo attachesTo = AttachesTo
            .builder("mount")
            .build();
        BlockStorageRequirement localStorage = BlockStorageRequirement
            .builder(attachesTo)
            .build();
        OsCapability osCapability = OsCapability
            .builder()
            .distribution(OsCapability.Distribution.UBUNTU)
            .type(OsCapability.Type.LINUX)
            .version("16.04")
            .build();
        Compute computeNode = Compute
            .builder("server", osCapability, computeAdminEndpointCap, localStorage)
            .host(createContainerCapability())
            .build();
        return computeNode;
    }

    private ContainerCapability createContainerCapability() {
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

    private MysqlDbms createMysqlDbms(Compute compute) {

        MysqlDbms mysqlDbms = MysqlDbms.builder(
            "mysql_dbms",
            "geheim12")
            .host(HostRequirement.builder().fulfiller(compute).build()) //TODO Relationship?
            .port(3306)
            .build();

        return mysqlDbms;
    }

    private MysqlDatabase createMysqlDatabase(MysqlDbms dbms) {
        Operation databaseConfigureOperation = Operation.builder()
            .implementationArtifact("mysql/createtable.sql")
            .build();

        StandardLifecycle lifecycle = StandardLifecycle.builder()
            .configure(databaseConfigureOperation)
            .build();

        DatabaseEndpointCapability dbEndpointCapability = DatabaseEndpointCapability
            .builder("127.0.0.1", new Port(3306))
            .build();

        MysqlDatabase mydb = MysqlDatabase
            .builder("my_db", "DBNAME", dbEndpointCapability)
            .standardLifecycle(lifecycle)
            .host(MysqlDbmsRequirement.builder().fulfiller(dbms).build()) //TODO Relationship?
            .build();

        return mydb;
    }

    private Apache createApache(Compute compute) {
        ContainerCapability containerCapability = createContainerCapability();
        DatabaseEndpointCapability apacheEndpoint = DatabaseEndpointCapability
            .builder("127.0.0.1", new Port(3306))
            .build();
        AdminEndpointCapability adminEndpointCapability = AdminEndpointCapability
            .builder("127.0.0.1", new Port(80))
            .build();

        Apache webServer = Apache.builder(
            "apache_web_server",
            containerCapability,
            apacheEndpoint,
            adminEndpointCapability)
            .host(HostRequirement.builder().fulfiller(compute).build()) //TODO Relationship
            .databaseEndpoint(apacheEndpoint)
            .build();

        return webServer;
    }

    private WebApplication createWebApplication(Apache webserver, MysqlDatabase database) {
        EndpointCapability endpointCapability = EndpointCapability
            .builder("127.0.0.1", new Port(80))
            .build();
        Set<String> appDependencies = new HashSet<>();
        appDependencies.add("my_app/myphpapp.php");
        appDependencies.add("my_app/mysql-credentials.php");
        Operation appCreate = Operation.builder()
            .implementationArtifact("my_app/create_myphpapp.sh")
            .dependencies(appDependencies)
            .build();

        Set<OperationVariable> appInputs = new HashSet<>();
        appInputs.add(new OperationVariable("database_host")); //TODO what to put in here?
        appInputs.add(new OperationVariable("database_password", database.getPassword().get()));
        appInputs.add(new OperationVariable("database_name", database.getDatabaseName()));
        appInputs.add(new OperationVariable("database_user", database.getUser().get()));
        appInputs.add(new OperationVariable("database_port", database.getPort().get().toString()));

        Operation appConfigure = Operation.builder()
            .implementationArtifact("my_app/configure_myphpapp.sh")
            .inputs(appInputs)
            .build();

        StandardLifecycle webAppLifecycle = StandardLifecycle.builder()
            .create(appCreate)
            .configure(appConfigure)
            .build();

        WebApplication webApplication = WebApplication
            .builder("my_app", endpointCapability)
            .host(WebServerRequirement.builder().fulfiller(webserver).build()) //TODO Relationship
            .standardLifecycle(webAppLifecycle)
            .build();

        return webApplication;
    }
}
