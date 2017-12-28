package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.capability.ContainerCapability;
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
        MysqlDatabase mydb = MysqlDatabase
            .builder("my-db", "DBNAME")
            .mysqlHost(MysqlDbmsRequirement.builder().fulfiller(dbms).build())
            .build();

        return mydb;
    }

    private Apache createApache(Compute compute) {
        ContainerCapability containerCapability = createContainerCapability();
        Apache webServer = Apache.builder(
            "apache-web-server")
            .containerHost(containerCapability)
            .host(HostRequirement.builder().fulfiller(compute).build())
            .build();
        return webServer;
    }

    private WebApplication createWebApplication(Apache webserver) {
        Set<String> appDependencies = new HashSet<>();
        appDependencies.add("my_app/myphpapp.php");
        appDependencies.add("my_app/mysql-credentials.php");
        Operation appCreate = Operation.builder()
            .artifact(Artifact.builder("artifact", "my_app/create_myphpapp.sh").build())
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
            .artifact(Artifact.builder("myartifact", "my_app/configure_myphpapp.sh").build())
            .inputs(appInputs)
            .build();

        StandardLifecycle webAppLifecycle = StandardLifecycle.builder()
            .create(appCreate)
            .configure(appConfigure)
            .build();
        WebApplication webApplication = WebApplication
            .builder("my-app")
            .host(
                WebServerRequirement.builder()
                    .capability(ContainerCapability.builder().build())
                    .fulfiller(webserver)
                    .build())
            .standardLifecycle(webAppLifecycle)
            .build();

        return webApplication;
    }
}
