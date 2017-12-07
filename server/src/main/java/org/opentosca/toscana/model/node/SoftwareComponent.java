package org.opentosca.toscana.model.node;

import java.util.Optional;

import org.opentosca.toscana.model.datatype.Credential;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.HostRequirement;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Represents a generic software component that can be managed and run by a Compute Node.
 (TOSCA Simple Profile in YAML Version 1.1, p.170)
 */
@Data
public class SoftwareComponent extends RootNode {

    /**
     The optional software component’s version.
     (TOSCA Simple Profile in YAML Version 1.1, p.170)
     */
    private final String componentVersion;

    /**
     The optional credential that can be used to authenticate to the software component.
     (TOSCA Simple Profile in YAML Version 1.1, p.170)
     */
    private final Credential adminCredential;

    private final HostRequirement host;

    @Builder
    protected SoftwareComponent(String componentVersion,
                                Credential adminCredential,
                                HostRequirement host,
                                String nodeName,
                                StandardLifecycle standardLifecycle,
                                String description) {
        super(nodeName, standardLifecycle, description);
        this.componentVersion = componentVersion;
        this.adminCredential = adminCredential;
        this.host = (host == null) ? HostRequirement.builder().build() : host;

        requirements.add(this.host);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static SoftwareComponentBuilder builder(String nodeName) {
        return new SoftwareComponentBuilder()
            .nodeName(nodeName);
    }

    public Optional<String> getComponentVersion() {
        return Optional.ofNullable(componentVersion);
    }

    public Optional<Credential> getAdminCredential() {
        return Optional.ofNullable(adminCredential);
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }

    public static class SoftwareComponentBuilder extends RootNodeBuilder {
        private String componentVersion;
        private Credential adminCredential;
        private HostRequirement host;
        private String nodeName;
        private StandardLifecycle standardLifecycle;
        private String description;

        SoftwareComponentBuilder() {
        }

        public SoftwareComponentBuilder componentVersion(String componentVersion) {
            this.componentVersion = componentVersion;
            return this;
        }

        public SoftwareComponentBuilder adminCredential(Credential adminCredential) {
            this.adminCredential = adminCredential;
            return this;
        }

        public SoftwareComponentBuilder host(HostRequirement host) {
            this.host = host;
            return this;
        }

        @Override
        public SoftwareComponentBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        @Override
        public SoftwareComponentBuilder standardLifecycle(StandardLifecycle standardLifecycle) {
            this.standardLifecycle = standardLifecycle;
            return this;
        }

        @Override
        public SoftwareComponentBuilder description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public SoftwareComponent build() {
            return new SoftwareComponent(componentVersion, adminCredential, host, nodeName, standardLifecycle, description);
        }

        public String toString() {
            return "SoftwareComponent.SoftwareComponentBuilder(componentVersion=" + this.componentVersion + ", adminCredential=" + this.adminCredential + ", host=" + this.host + ", nodeName=" + this.nodeName + ", standardLifecycle=" + this.standardLifecycle + ", description=" + this.description + ")";
        }
    }
}

