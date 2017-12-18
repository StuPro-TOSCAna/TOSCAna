package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.util.SizeConverter;
import org.opentosca.toscana.core.parse.converter.util.SizeConverter.Unit;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.ComputeCapability;
import org.opentosca.toscana.model.capability.ComputeCapability.ComputeCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class ComputeCapabilityVisitor<CapabilityT extends ComputeCapability, BuilderT extends ComputeCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    private final static String NAME_PROPERTY = "name";
    private final static String NUM_CPUS_PROPERTY = "num_cpus";
    private final static String CPU_FREQUENCY_PROPERTY = "cpu_frequency";
    private final static String DISK_SIZE_PROPERTY = "disk_size";
    private final static String MEM_SIZE_PROPERTY = "mem_size";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case NAME_PROPERTY:
                builder.resourceName((String) value);
                break;
            case NUM_CPUS_PROPERTY:
                builder.numCpus((Integer) value);
                break;
            case CPU_FREQUENCY_PROPERTY:
                builder.cpuFrequencyInGhz((Double) value);
                break;
            case DISK_SIZE_PROPERTY:
                Integer diskSize = new SizeConverter().convert(value, Unit.MB, Unit.MB);
                builder.diskSizeInMB(diskSize);
                break;
            case MEM_SIZE_PROPERTY:
                Integer memSize = new SizeConverter().convert(value, Unit.MB, Unit.MB);
                builder.memSizeInMB(memSize);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return ComputeCapabilityBuilder.class;
    }
}
