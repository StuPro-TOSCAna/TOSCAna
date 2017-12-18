package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.ScalableCapability;
import org.opentosca.toscana.model.capability.ScalableCapability.ScalableCapabilityBuilder;
import org.opentosca.toscana.model.datatype.Range;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class ScalableCapabilityVisitor<CapabilityT extends ScalableCapability, BuilderT extends ScalableCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    private final static String MIN_INSTANCES_PROPERTY = "min_instances";
    private final static String MAX_INSTANCES_PROPERTY = "max_instances";
    private final static String DEFAULT_INSTANCES_PROPERTY = "default_instances";

    private Integer min;
    private Integer max;

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case MIN_INSTANCES_PROPERTY:
                min = (Integer) value;
                if (max != null) {
                    builder.scaleRange(new Range(min, max));
                }

                break;
            case MAX_INSTANCES_PROPERTY:
                max = (Integer) value;
                if (min != null) {
                    builder.scaleRange(new Range(min, max));
                }
                break;
            case DEFAULT_INSTANCES_PROPERTY:
                builder.defaultInstances((Integer) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return ScalableCapabilityBuilder.class;
    }
}
