package org.opentosca.toscana.model.capability;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 The type indicates capabilities of a Docker runtime environment (client).
 (TOSCA Simple Profile in YAML Version 1.1, p. 219)
 */
@Data
public class DockerContainerCapability extends ContainerCapability {

    /**
     The Docker version capability (i.e., the versions supported by the capability).
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final Set<String> versions;

    /**
     Indicates that all ports (ranges) listed in the dockerfile using the EXPOSE keyword shall be published.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final boolean publishAll;

    /**
     Set of port mappings from source (Docker container) to target (host) ports to publish.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final Set<PortSpec> publishPorts;

    /**
     Set of port mappings from source (Docker container) to expose to other Docker containers
     (not accessible outside host).
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final Set<PortSpec> exposePorts;

    /**
     The dockerfile VOLUME command which is used to enable access from the Docker container
     to a directory on the host machine.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final Set<String> volumes;

    /**
     The optional identifier of an existing host resource that should be used to run this container on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final String hostId;

    /**
     The optional identifier of an existing storage volume (resource)
     that should be used to create the container’s mount point(s) on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    private final String volumeId;

    @Builder
    protected DockerContainerCapability(@Singular Set<String> versions,
                                        boolean publishAll,
                                        @Singular Set<PortSpec> publishPorts,
                                        @Singular Set<PortSpec> exposePorts,
                                        @Singular Set<String> volumes,
                                        String hostId,
                                        String volumeId,
                                        String resourceName,
                                        Integer numCpus,
                                        Double cpuFrequencyInGhz,
                                        Integer diskSizeInMB,
                                        Integer memSizeInMB,
                                        Set<Class<? extends RootNode>> validSourceTypes,
                                        Range occurrence) {
        super(resourceName, numCpus, cpuFrequencyInGhz, diskSizeInMB, memSizeInMB, validSourceTypes, occurrence);
        this.versions = Objects.requireNonNull(versions);
        this.publishAll = publishAll;
        this.publishPorts = Objects.requireNonNull(publishPorts);
        this.exposePorts = Objects.requireNonNull(exposePorts);
        this.volumes = Objects.requireNonNull(volumes);
        this.hostId = hostId;
        this.volumeId = volumeId;
    }

    public static DockerContainerCapability getFallback(DockerContainerCapability c) {
        return (c == null) ? DockerContainerCapability.builder().build() : c;
    }

    /**
     @return {@link #hostId}
     */
    public Optional<String> getHostId() {
        return Optional.ofNullable(hostId);
    }

    /**
     @return {@link #volumeId}
     */
    public Optional<String> getVolumeId() {
        return Optional.ofNullable(volumeId);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class DockerContainerCapabilityBuilder extends ContainerCapabilityBuilder {
    }
}
