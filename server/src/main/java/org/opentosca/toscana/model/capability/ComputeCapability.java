package org.opentosca.toscana.model.capability;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Indicates that the node can provide hosting on a named compute resource.
 (TOSCA Simple Profile in YAML Version 1.1, p. 150)
 */

@EqualsAndHashCode
@ToString
public class ComputeCapability extends Capability {

    /**
     Optional name (or identifier) of a specific compute resource for hosting.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    public static ToscaKey<String> RESOURCE_NAME = new ToscaKey<>(PROPERTIES, "name");

    /**
     Optional number of (actual or virtual) CPUs associated with the {@link Compute} node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    public static ToscaKey<Integer> NUM_CPUS = new ToscaKey<>(PROPERTIES, "num_cpus")
        .type(Integer.class);

    /**
     Optional operating frequency of CPU's core.
     This property expresses the expected frequency of one CPU as provided by {@link #NUM_CPUS}
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    public static ToscaKey<Double> CPU_FREQUENCY_IN_GHZ = new ToscaKey<>(PROPERTIES, "cpu_frequency")
        .type(Double.class);

    /**
     Optional size of the local disk space available to applications running on the {@link Compute} node, specified in MB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    public static ToscaKey<Integer> DISK_SIZE_IN_MB = new ToscaKey<>(PROPERTIES, "disk_size")
        .type(SizeUnit.class).directive(SizeUnit.FROM, SizeUnit.Unit.MB).directive(SizeUnit.TO, SizeUnit.Unit.MB);

    /**
     Optional size of memory available to applications running on the {@link Compute} node, specified in MB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    public static ToscaKey<Integer> MEM_SIZE_IN_MB = new ToscaKey<>(PROPERTIES, "mem_size")
        .type(SizeUnit.class).directive(SizeUnit.FROM, SizeUnit.Unit.MB).directive(SizeUnit.TO, SizeUnit.Unit.MB);

    public ComputeCapability(MappingEntity mappingEntity) {
        super(mappingEntity);
    }

    /**
     @return {@link #RESOURCE_NAME}
     */
    public Optional<String> getResourceName() {
        return Optional.ofNullable(get(RESOURCE_NAME));
    }

    /**
     Sets {@link #RESOURCE_NAME}
     */
    public ComputeCapability setResourceName(String resourceName) {
        set(RESOURCE_NAME, resourceName);
        return this;
    }

    /**
     @return {@link #NUM_CPUS}
     */
    public Optional<Integer> getNumCpus() {
        return Optional.ofNullable(get(NUM_CPUS));
    }

    /**
     Sets {@link #NUM_CPUS}
     */
    public ComputeCapability setNumCpus(Integer numCpus) {
        if (numCpus < 1) {
            throw new IllegalArgumentException(String.format(
                "numCpus must be greater than 0, but was %d", numCpus));
        }
        set(NUM_CPUS, numCpus);
        return this;
    }

    /**
     @return {@link #CPU_FREQUENCY_IN_GHZ}
     */
    public Optional<Double> getCpuFrequencyInGhz() {
        return Optional.ofNullable(get(CPU_FREQUENCY_IN_GHZ));
    }

    /**
     Sets {@link #CPU_FREQUENCY_IN_GHZ}
     */
    public ComputeCapability setCpuFrequencyInGhz(Double cpuFrequencyInGhz) {
        if (cpuFrequencyInGhz < 0.1) {
            throw new IllegalArgumentException(String.format(
                "cpuFrequency min value is 0.1, but was %s", cpuFrequencyInGhz));
        }
        set(CPU_FREQUENCY_IN_GHZ, cpuFrequencyInGhz);
        return this;
    }

    /**
     @return {@link #DISK_SIZE_IN_MB}
     */
    public Optional<Integer> getDiskSizeInMb() {
        return Optional.ofNullable(get(DISK_SIZE_IN_MB));
    }

    /**
     Sets {@link #DISK_SIZE_IN_MB}
     */
    public ComputeCapability setDiskSizeInMb(Integer diskSizeInMb) {
        if (diskSizeInMb < 0) {
            throw new IllegalArgumentException(String.format(
                "diskSize min value is 0, but was %d", diskSizeInMb));
        }
        set(DISK_SIZE_IN_MB, diskSizeInMb);
        return this;
    }

    /**
     @return {@link #MEM_SIZE_IN_MB}
     */
    public Optional<Integer> getMemSizeInMb() {
        return Optional.ofNullable(get(MEM_SIZE_IN_MB));
    }

    /**
     Sets {@link #MEM_SIZE_IN_MB}
     */
    public ComputeCapability setMemSizeInMb(Integer memSizeInMb) {
        if (memSizeInMb < 0) {
            throw new IllegalArgumentException(String.format(
                "memSize min value is 0, but was %d", memSizeInMb));
        }
        set(MEM_SIZE_IN_MB, memSizeInMb);
        return this;
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }
}
