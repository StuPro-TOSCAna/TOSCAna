package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.datatype.PortSpec;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The type indicates capabilities of a Docker runtime environment (client).
 (TOSCA Simple Profile in YAML Version 1.1, p. 219)
 */
@EqualsAndHashCode
@ToString
public class DockerContainerCapability extends ContainerCapability {

    /**
     The Docker version capability (i.e., the versions supported by the capability).
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<Set<String>> VERSIONS = new ToscaKey<>(PROPERTIES, "version");

    /**
     Indicates that all ports (ranges) listed in the dockerfile using the EXPOSE keyword shall be published.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<Boolean> PUBLISH_ALL = new ToscaKey<>(PROPERTIES, "publish_all")
        .type(Boolean.class);

    /**
     Set of port mappings from source (Docker container) to target (host) ports to publish.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<Set<PortSpec>> PUBLISH_PORTS = new ToscaKey<>(PROPERTIES, "publish_ports")
        .type(PortSpec.class);

    /**
     Set of port mappings from source (Docker container) to expose to other Docker containers
     (not accessible outside host).
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<Set<PortSpec>> EXPOSE_PORTS = new ToscaKey<>(PROPERTIES, "expose_ports")
        .type(PortSpec.class);

    /**
     The dockerfile VOLUME command which is used to enable access from the Docker container
     to a directory on the host machine.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<Set<String>> VOLUMES = new ToscaKey<>(PROPERTIES, "volumes");

    /**
     The optional identifier of an existing host resource that should be used to run this container on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<String> HOST_ID = new ToscaKey<>(PROPERTIES, "host_id");

    /**
     The optional identifier of an existing storage volume (resource)
     that should be used to create the container’s mount point(s) on.
     (TOSCA Simple Profile in YAML Version 1.1, p. 220)
     */
    public static ToscaKey<String> VOLUME_ID = new ToscaKey<>(PROPERTIES, "volume_id");

    public DockerContainerCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #HOST_ID}
     */
    public Optional<String> getHostId() {
        return Optional.ofNullable(get(HOST_ID));
    }

    /**
     Sets {@link #HOST_ID}
     */
    public DockerContainerCapability setHostId(String hostId) {
        set(HOST_ID, hostId);
        return this;
    }

    /**
     @return {@link #HOST_ID}
     */
    public Optional<String> getVolumeId() {
        return Optional.ofNullable(get(VOLUME_ID));
    }

    /**
     Sets {@link #VOLUME_ID}
     */
    public DockerContainerCapability setVolumeId(String volumeId) {
        set(VOLUME_ID, volumeId);
        return this;
    }

    /**
     @return {@link #PUBLISH_ALL}
     */
    public Boolean getPublishAll() {
        return get(PUBLISH_ALL);
    }

    /**
     Sets {@link #PUBLISH_ALL}
     */
    public DockerContainerCapability setPublishAll(Boolean publishAll) {
        set(PUBLISH_ALL, publishAll);
        return this;
    }

    /**
     @return {@link #PUBLISH_PORTS}
     */
    public Set<PortSpec> getPublishPorts() {
        return get(PUBLISH_PORTS);
    }

    /**
     Sets {@link #PUBLISH_PORTS}
     */
    public DockerContainerCapability setPublishPorts(Set<PortSpec> publishPorts) {
        set(PUBLISH_PORTS, publishPorts);
        return this;
    }

    /**
     @return {@link #EXPOSE_PORTS}
     */
    public Set<PortSpec> getExposePorts() {
        return get(EXPOSE_PORTS);
    }

    /**
     Sets {@link #EXPOSE_PORTS}
     */
    public DockerContainerCapability setExposePorts(Set<PortSpec> exposePorts) {
        set(EXPOSE_PORTS, exposePorts);
        return this;
    }

    /**
     @return {@link #VERSIONS}
     */
    public Set<String> getVersions() {
        return get(VERSIONS);
    }

    /**
     Sets {@link #VERSIONS}
     */
    public DockerContainerCapability setVersions(Set<String> versions) {
        set(VERSIONS, versions);
        return this;
    }

    /**
     @return {@link #VOLUMES}
     */
    public Set<String> getVolumes() {
        return get(VOLUMES);
    }

    /**
     Sets {@link #VOLUMES}
     */
    public DockerContainerCapability setVolumes(Set<String> volumes) {
        set(VOLUMES, volumes);
        return this;
    }
}
