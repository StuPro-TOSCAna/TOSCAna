package org.opentosca.toscana.model.capability;

import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.visitor.CapabilityVisitor;

import lombok.Builder;
import lombok.Data;

/**
 Indicates that the node can provide hosting on a named compute resource.
 (TOSCA Simple Profile in YAML Version 1.1, p. 150)
 */

@Data
public class ComputeCapability extends Capability {

    /**
     Optional name (or identifier) of a specific compute resource for hosting.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    private final String resourceName;

    /**
     Optional number of (actual or virtual) CPUs associated with the {@link Compute} node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    @Min(1)
    private final Integer numCpus;

    /**
     Optional operating frequency of CPU's core.
     This property expresses the expected frequency of one CPU as provided by {@link #numCpus}
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    @DecimalMin("0.1")
    private final Double cpuFrequencyInGhz;

    /**
     Optional size of the local disk space available to applications running on the {@link Compute} node, specified in MB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    @Min(0)
    private final Integer diskSizeInMB;

    /**
     Optional size of memory available to applications running on the {@link Compute} node, specified in MB.
     (TOSCA Simple Profile in YAML Version 1.1, p. 150)
     */
    @Min(0)
    private final Integer memSizeInMB;

    @Builder
    protected ComputeCapability(String resourceName,
                                Integer numCpus,
                                Double cpuFrequencyInGhz,
                                Integer diskSizeInMB,
                                Integer memSizeInMB,
                                Set<Class<? extends RootNode>> validSourceTypes,
                                Range occurrence) {
        super(validSourceTypes, occurrence);
        if (numCpus != null && numCpus < 1) {
            throw new IllegalArgumentException(String.format(
                "numCpus must be greater than 0, but was %d", numCpus));
        }
        if (cpuFrequencyInGhz != null && cpuFrequencyInGhz < 0.1) {
            throw new IllegalArgumentException(String.format(
                "cpuFrequency min value is 0.1, but was %s", cpuFrequencyInGhz));
        }
        if (diskSizeInMB != null && diskSizeInMB < 0) {
            throw new IllegalArgumentException(String.format(
                "diskSize min value is 0, but was %d", diskSizeInMB));
        }
        if (memSizeInMB != null && memSizeInMB < 0) {
            throw new IllegalArgumentException(String.format(
                "memSize min value is 0, but was %d", memSizeInMB));
        }
        this.resourceName = resourceName;
        this.numCpus = numCpus;
        this.cpuFrequencyInGhz = cpuFrequencyInGhz;
        this.diskSizeInMB = diskSizeInMB;
        this.memSizeInMB = memSizeInMB;
    }

    /**
     @return {@link #resourceName}
     */
    public Optional<String> getResourceName() {
        return Optional.ofNullable(resourceName);
    }

    /**
     @return {@link #numCpus}
     */
    public Optional<Integer> getNumCpus() {
        return Optional.ofNullable(numCpus);
    }

    /**
     @return {@link #cpuFrequencyInGhz}
     */
    public Optional<Double> getCpuFrequencyInGhz() {
        return Optional.ofNullable(cpuFrequencyInGhz);
    }

    /**
     @return {@link #diskSizeInMB}
     */
    public Optional<Integer> getDiskSizeInMB() {
        return Optional.ofNullable(diskSizeInMB);
    }

    /**
     @return {@link #memSizeInMB}
     */
    public Optional<Integer> getMemSizeInMB() {
        return Optional.ofNullable(memSizeInMB);
    }

    @Override
    public void accept(CapabilityVisitor v) {
        v.visit(this);
    }

    public static class ComputeCapabilityBuilder extends CapabilityBuilder {
    }
}
