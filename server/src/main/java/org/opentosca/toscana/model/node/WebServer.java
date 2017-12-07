package org.opentosca.toscana.model.node;

import java.util.Objects;

import org.opentosca.toscana.model.capability.AdminEndpointCapability;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.capability.EndpointCapability;
import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

/**
 Represents an abstract software component or service that is capable of hosting and providing management operations
 for one or more {@link WebApplication} nodes.
 <p>
 This node SHALL export both a secure endpoint capability ({@link #adminEndpoint}), typically for
 administration, as well as a regular endpoint ({@link #dataEndpoint}) for serving data.
 (TOSCA Simple Profile in YAML Version 1.1, p.171)
 */
public class WebServer extends SoftwareComponent {

    private final EndpointCapability dataEndpoint;

    private final AdminEndpointCapability adminEndpoint;

    private final ContainerCapability containerHost;

    protected WebServer(String componentVersion,
                        Credential adminCredential,
                        HostRequirement host,
                        ContainerCapability containerHost,
                        EndpointCapability dataEndpoint,
                        AdminEndpointCapability adminEndpoint,
                        String nodeName,
                        StandardLifecycle standardLifecycle,
                        String description) {
        super(componentVersion, adminCredential, host, nodeName, standardLifecycle, description);
        this.containerHost = Objects.requireNonNull(containerHost);
        this.dataEndpoint = Objects.requireNonNull(dataEndpoint);
        this.adminEndpoint = Objects.requireNonNull(adminEndpoint);

        capabilities.add(this.containerHost);
        capabilities.add(this.dataEndpoint);
        capabilities.add(this.adminEndpoint);
    }

    /**
     @param nodeName      {@link #nodeName}
     @param containerHost {@link #containerHost}
     @param host          {@link #host}
     @param dataEndpoint  {@link #dataEndpoint}
     @param adminEndpoint {@link #adminEndpoint}
     */
    public static WebServerBuilder builder(String nodeName,
                                           ContainerCapability containerHost,
                                           HostRequirement host,
                                           EndpointCapability dataEndpoint,
                                           AdminEndpointCapability adminEndpoint) {
        return new WebServerBuilder()
            .nodeName(nodeName)
            .containerHost(containerHost)
            .host(host)
            .dataEndpoint(dataEndpoint)
            .adminEndpoint(adminEndpoint);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public EndpointCapability getDataEndpoint() {
        return this.dataEndpoint;
    }

    public AdminEndpointCapability getAdminEndpoint() {
        return this.adminEndpoint;
    }

    public ContainerCapability getContainerHost() {
        return this.containerHost;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WebServer)) return false;
        final WebServer other = (WebServer) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$dataEndpoint = this.getDataEndpoint();
        final Object other$dataEndpoint = other.getDataEndpoint();
        if (this$dataEndpoint == null ? other$dataEndpoint != null : !this$dataEndpoint.equals(other$dataEndpoint))
            return false;
        final Object this$adminEndpoint = this.getAdminEndpoint();
        final Object other$adminEndpoint = other.getAdminEndpoint();
        if (this$adminEndpoint == null ? other$adminEndpoint != null : !this$adminEndpoint.equals(other$adminEndpoint))
            return false;
        final Object this$containerHost = this.getContainerHost();
        final Object other$containerHost = other.getContainerHost();
        if (this$containerHost == null ? other$containerHost != null : !this$containerHost.equals(other$containerHost))
            return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + super.hashCode();
        final Object $dataEndpoint = this.getDataEndpoint();
        result = result * PRIME + ($dataEndpoint == null ? 43 : $dataEndpoint.hashCode());
        final Object $adminEndpoint = this.getAdminEndpoint();
        result = result * PRIME + ($adminEndpoint == null ? 43 : $adminEndpoint.hashCode());
        final Object $containerHost = this.getContainerHost();
        result = result * PRIME + ($containerHost == null ? 43 : $containerHost.hashCode());
        return result;
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof WebServer;
    }

    public String toString() {
        return "WebServer(dataEndpoint=" + this.getDataEndpoint() + ", adminEndpoint=" + this.getAdminEndpoint() + ", containerHost=" + this.getContainerHost() + ")";
    }

    public static class WebServerBuilder extends SoftwareComponentBuilder {
        private String componentVersion;
        private Credential adminCredential;
        private HostRequirement host;
        private ContainerCapability containerHost;
        private EndpointCapability dataEndpoint;
        private AdminEndpointCapability adminEndpoint;
        private String nodeName;
        private StandardLifecycle standardLifecycle;
        private String description;

        WebServerBuilder() {
        }

        @Override
        public WebServerBuilder componentVersion(String componentVersion) {
            this.componentVersion = componentVersion;
            return this;
        }

        @Override
        public WebServerBuilder adminCredential(Credential adminCredential) {
            this.adminCredential = adminCredential;
            return this;
        }

        @Override
        public WebServerBuilder host(HostRequirement host) {
            this.host = host;
            return this;
        }

        public WebServerBuilder containerHost(ContainerCapability containerHost) {
            this.containerHost = containerHost;
            return this;
        }

        public WebServerBuilder dataEndpoint(EndpointCapability dataEndpoint) {
            this.dataEndpoint = dataEndpoint;
            return this;
        }

        public WebServerBuilder adminEndpoint(AdminEndpointCapability adminEndpoint) {
            this.adminEndpoint = adminEndpoint;
            return this;
        }

        @Override
        public WebServerBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        @Override
        public WebServerBuilder standardLifecycle(StandardLifecycle standardLifecycle) {
            this.standardLifecycle = standardLifecycle;
            return this;
        }

        @Override
        public WebServerBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public WebServer build() {
            return new WebServer(componentVersion, adminCredential, host, containerHost, dataEndpoint, adminEndpoint, nodeName, standardLifecycle, description);
        }

        public String toString() {
            return "WebServer.WebServerBuilder(componentVersion=" + this.componentVersion + ", adminCredential=" + this.adminCredential + ", host=" + this.host + ", containerHost=" + this.containerHost + ", dataEndpoint=" + this.dataEndpoint + ", adminEndpoint=" + this.adminEndpoint + ", nodeName=" + this.nodeName + ", standardLifecycle=" + this.standardLifecycle + ", description=" + this.description + ")";
        }
    }
}
