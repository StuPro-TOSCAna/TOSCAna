package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 The default TOSCA type to express an Operating System capability for a node.
 (TOSCA Simple Profile in YAML Version 1.1, p. 157)
 */
@Data
public class OsCapability extends Capability {

    /**
     The optional Operating System architecture.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Architecture architecture;
    /**
     The optional Operating System type.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Type type;
    /**
     The optional Operating System distribution.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final Distribution distribution;
    /**
     The optional Operating System version.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    private final String version;

    @Builder
    protected OsCapability(Architecture architecture,
                           Type type,
                           Distribution distribution,
                           String version,
                           Set<Class<? extends RootNode>> validSourceTypes,
                           Range occurrence) {
        super(validSourceTypes, occurrence);
        this.architecture = architecture;
        this.type = type;
        this.distribution = distribution;
        this.version = version;
    }

    public static OsCapability getFallback(OsCapability os) {
        return (os == null) ? OsCapability.builder().build() : os;
    }

    /**
     @return {@link #architecture}
     */
    public Optional<Architecture> getArchitecture() {
        return Optional.ofNullable(architecture);
    }

    /**
     @return {@link #type}
     */
    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    /**
     @return {@link #distribution}
     */
    public Optional<Distribution> getDistribution() {
        return Optional.ofNullable(distribution);
    }

    /**
     @return {@link #version}
     */
    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public enum Architecture {
        x86_32,
        x86_64,
        POWER_PC
        // might grow
    }

    public enum Type {
        AIX,
        LINUX,
        MAC,
        WINDOWS,
        // might grow
    }

    public enum Distribution {
        ARCH,
        DEBIAN,
        FEDORA,
        RHEL,
        UBUNTU,
        CENTOS,
        ALPINE,
        BUSYBOX,
        OPEN_SUSE,
        // might grow
    }

    public static class OsCapabilityBuilder extends CapabilityBuilder {
    }
}
