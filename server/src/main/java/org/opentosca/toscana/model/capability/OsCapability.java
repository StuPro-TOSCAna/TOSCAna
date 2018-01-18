package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

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
    public static ToscaKey<Architecture> ARCHITECTURE = new ToscaKey<>(PROPERTIES, "architecture")
        .type(Architecture.class);
    /**
     The optional Operating System type.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    public static ToscaKey<Type> TYPE = new ToscaKey<>(PROPERTIES, "type")
        .type(Type.class);
    /**
     The optional Operating System distribution.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    public static ToscaKey<Distribution> DISTRIBUTION = new ToscaKey<>(PROPERTIES, "distribution")
        .type(Distribution.class);
    /**
     The optional Operating System version.
     (TOSCA Simple Profile in YAML Version 1.1, p. 157)
     */
    public static ToscaKey<String> VERSION = new ToscaKey<>(PROPERTIES, "version");

    public OsCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    /**
     @return {@link #ARCHITECTURE}
     */

    public Optional<Architecture> getArchitecture() {
        return Optional.ofNullable(get(ARCHITECTURE));
    }

    /**
     Sets {@link #ARCHITECTURE}
     */
    public OsCapability setArchitecture(Architecture architecture) {
        set(ARCHITECTURE, architecture);
        return this;
    }

    /**
     @return {@link #TYPE}
     */

    public Optional<Type> getType() {
        return Optional.ofNullable(get(TYPE));
    }

    /**
     Sets {@link #TYPE}
     */
    public OsCapability setType(Type type) {
        set(TYPE, type);
        return this;
    }

    /**
     @return {@link #DISTRIBUTION}
     */

    public Optional<Distribution> getDistribution() {
        return Optional.ofNullable(get(DISTRIBUTION));
    }

    /**
     Sets {@link #DISTRIBUTION}
     */
    public OsCapability setDistribution(Distribution distribution) {
        set(DISTRIBUTION, distribution);
        return this;
    }

    /**
     @return {@link #VERSION}
     */

    public Optional<String> getVersion() {
        return Optional.ofNullable(get(VERSION));
    }

    /**
     Sets {@link #VERSION}
     */
    public OsCapability setVersion(String version) {
        set(VERSION, version);
        return this;
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
}
