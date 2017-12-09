package org.opentosca.toscana.plugins.testdata;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.AttachmentCapability;
import org.opentosca.toscana.model.capability.BindableCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.DatabaseEndpointCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.requirement.BlockStorageRequirement;
import org.opentosca.toscana.model.requirement.MysqlDbmsRequirement;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.Range;
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
import org.opentosca.toscana.model.relation.HostedOn;

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

        testNodes.add(createComputeNode());
        testNodes.add(createMysqlDbms());
        testNodes.add(createMsqlDatabase());
        testNodes.add(createApache());
        testNodes.add(createWebApplication());
    }

    private Compute createComputeNode() {
        ContainerCapability containerCapability = createContainerCapabilityBuilder().build();
        AdminEndpointCapability computeAdminEndpointCap = AdminEndpointCapability
            .builder("127.0.0.1")
            .port(new Port(80)).build();
        ScalableCapability scalableCapability = ScalableCapability.builder(Range.EXACTLY_ONCE).build();
        BindableCapability bindableCapability = BindableCapability.builder().build();
        AttachesTo attachesTo = AttachesTo.builder("mount").build();
        AttachmentCapability attachmentCapability = AttachmentCapability.builder().build();

        BlockStorageRequirement localStorage =
            BlockStorageRequirement.builder(attachmentCapability, attachesTo).build();

        OsCapability osCapability = OsCapability.builder()
            .distribution(OsCapability.Distribution.UBUNTU)
            .type(OsCapability.Type.LINUX)
            .version("16.04")
            .build();

        Compute computeNode = Compute.builder("server", osCapability, computeAdminEndpointCap, scalableCapability,
            bindableCapability, localStorage).host(containerCapability).build();
        return computeNode;
    }

    private ContainerCapability.ContainerCapabilityBuilder createContainerCapabilityBuilder() {
        Set<Class<? extends RootNode>> validSourceTypes = new HashSet<>();
        validSourceTypes.add(Compute.class);
        validSourceTypes.add(MysqlDbms.class);

        ContainerCapability.ContainerCapabilityBuilder containerCapabilityBuilder = ContainerCapability.builder()
            .memSizeInMB(1024)
            .diskSizeInMB(2000)
            .numCpus(1)
            .name("host")
            .validSourceTypes(validSourceTypes);

        return containerCapabilityBuilder;
    }

    private MysqlDbms createMysqlDbms() {
        Operation dbmsOperation = Operation.builder()
            .implementationArtifact("mysql_dbms/mysql_dbms_configure.sh")
            .input(new OperationVariable("db_root_password")).build();

        StandardLifecycle lifecycle = StandardLifecycle.builder()
            .configure(dbmsOperation)
            .build();

        HostRequirement hostedOnRequirement = getHostedOnServerRequirement();

        ContainerCapability.ContainerCapabilityBuilder capabilityBuilder = ContainerCapability.builder();

        MysqlDbms mysqlDbms = MysqlDbms.builder(
            "mysql_dbms",
            "geheim",
            hostedOnRequirement,
            capabilityBuilder
        ).port(3306).lifecycle(lifecycle).build();

        return mysqlDbms;
    }

    private MysqlDatabase createMsqlDatabase() {
        DatabaseEndpointCapability dbEndpointCapability = DatabaseEndpointCapability.builder("127.0.0.1")
            .port(new Port(3306))
            .build();
        ContainerCapability dbContainerCapability = ContainerCapability.builder().name("mysql_dbms").build();
        HostedOn hostedOn = HostedOn.builder().build();
        MysqlDbmsRequirement requirement = MysqlDbmsRequirement.builder(dbContainerCapability, hostedOn).fulfiller(createMysqlDbms())
            .build();

        MysqlDatabase mydb = MysqlDatabase.builder("my_db", "DBNAME", dbEndpointCapability,
            requirement)
            .build();

        return mydb;
    }

    private Apache createApache() {
        ContainerCapability containerCapability = createContainerCapabilityBuilder().build();
        DatabaseEndpointCapability apacheEndpoint = DatabaseEndpointCapability.builder("127.0.0.1")
            .port(new Port(3306)).build();
        AdminEndpointCapability adminEndpointCapability = AdminEndpointCapability.builder("127.0.0.1")
            .port(new Port(80)).build();

        HostRequirement hostedOnRequirement = getHostedOnServerRequirement();

        Apache webServer = Apache.builder(
            "apache_web_server",
            hostedOnRequirement,
            containerCapability,
            apacheEndpoint,
            adminEndpointCapability
        ).databaseEndpoint(apacheEndpoint).build();

        return webServer;
    }

    private HostRequirement getHostedOnServerRequirement() {
        ContainerCapability hostCapability = ContainerCapability.builder().name("server").build();

        return HostRequirement.builder(
            hostCapability,
            HostedOn.builder().build()
        ).build();
    }

    private WebApplication createWebApplication() {
        EndpointCapability endpointCapability = EndpointCapability.builder("127.0.0.1", new Port(80)).build();
        Set<String> appDependencies = new HashSet<>();
        appDependencies.add("my_app/myphpapp.php");
        appDependencies.add("my_app/mysql-credentials.php");
        Operation appCreate = Operation.builder().implementationArtifact("my_app/create_myphpapp.sh")
            .dependencies(appDependencies)
            .build();

        Set<OperationVariable> appInputs = new HashSet<>();
        appInputs.add(new OperationVariable("database_host"));
        appInputs.add(new OperationVariable("database_password"));
        appInputs.add(new OperationVariable("database_name"));
        appInputs.add(new OperationVariable("database_user"));
        OperationVariable dbPort = new OperationVariable("database_port");
        dbPort.setValue("3306");
        appInputs.add(dbPort);

        Operation appConfigure = Operation.builder().implementationArtifact("my_app/configure_myphpapp.sh")
            .inputs(appInputs)
            .build();

        StandardLifecycle webAppLifecycle = StandardLifecycle.builder()
            .create(appCreate)
            .configure(appConfigure)
            .build();
        WebApplication webApplication = WebApplication.builder("my_app", endpointCapability)
            .standardLifecycle(webAppLifecycle)
            .build();

        return webApplication;
    }
}
