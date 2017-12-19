package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
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

@SuppressWarnings({"Duplicates"})
public class KubernetesLampApp {

    private final Set<RootNode> testNodes = new HashSet<>();

    public Set<RootNode> getLampApp() {
        createLampModel();
        return testNodes;
    }

    public static Set<RootNode> getLampModel() {
        return new KubernetesLampApp().getLampApp();
    }

    private void createLampModel() {

        Compute compute = createComputeNode();
        Apache webserver = createApache(compute);
        MysqlDbms dbms = createMysqlDbms(compute);

        testNodes.add(compute);
        testNodes.add(webserver);
        testNodes.add(dbms);

        testNodes.add(createMysqlDatabase(dbms));
        testNodes.add(createWebApplication(webserver));
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

        ContainerCapability.ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(1024)
            .diskSizeInMB(2000)
            .numCpus(1)
            .validSourceTypes(validSourceTypes);

        return containerCapabilityBuilder.build();
    }

    private MysqlDbms createMysqlDbms(Compute compute) {
        MysqlDbms mysqlDbms = MysqlDbms.builder(
            "mysql-dbms",
            "geheim")
            .host(HostRequirement.builder().fulfiller(compute).build())
            .port(3306)
//            .lifecycle(lifecycle)
            .build();

        return mysqlDbms;
    }

    private MysqlDatabase createMysqlDatabase(MysqlDbms dbms) {
        DatabaseEndpointCapability dbEndpointCapability = DatabaseEndpointCapability
            .builder("127.0.0.1", new Port(3306))
            .build();

        Operation configure = Operation.builder().dependency("mysql_dbms/schema.sql").build();
        
        StandardLifecycle lifecycle = StandardLifecycle.builder().configure(configure).build();
        
        MysqlDatabase mydb = MysqlDatabase
            .builder("my-db", "DBNAME", dbEndpointCapability)
            .standardLifecycle(lifecycle)
            .host(MysqlDbmsRequirement.builder().fulfiller(dbms).build())
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
            "apache-web-server",
            containerCapability,
            apacheEndpoint,
            adminEndpointCapability)
            .host(HostRequirement.builder().fulfiller(compute).build())
            .databaseEndpoint(apacheEndpoint)
            .build();

        return webServer;
    }

    private WebApplication createWebApplication(Apache webserver) {
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
        appInputs.add(new OperationVariable("database_host", "my-db-service"));
        appInputs.add(new OperationVariable("database_password", "geheim"));
        appInputs.add(new OperationVariable("database_name", "DBNAME"));
        appInputs.add(new OperationVariable("database_user", "root"));
        OperationVariable dbPort = new OperationVariable("database_port", "3306");
        dbPort.setValue("3306");
        appInputs.add(dbPort);

        Operation appConfigure = Operation.builder()
            .implementationArtifact("my_app/configure_myphpapp.sh")
            .inputs(appInputs)
            .build();

        StandardLifecycle webAppLifecycle = StandardLifecycle.builder()
            .create(appCreate)
            .configure(appConfigure)
            .build();
        WebApplication webApplication = WebApplication
            .builder("my-app", endpointCapability)
            .host(
                WebServerRequirement.builder()
                    .capability(
                        ContainerCapability.builder().build()
                    ).fulfiller(webserver).build()
            )
            .standardLifecycle(webAppLifecycle)
            .build();

        return webApplication;
    }
}
