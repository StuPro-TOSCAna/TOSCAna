package org.opentosca.toscana.core.parse.converter.visitor.capability;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.capability.OsCapability.OsCapabilityBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class OsCapabilityVisitor<CapabilityT extends OsCapability, BuilderT extends OsCapabilityBuilder> extends CapabilityVisitor<CapabilityT, BuilderT> {

    private final static String ARCHITECTURE_PROPERTY = "architecture";
    private final static String TYPE_PROPERTY = "type";
    private final static String DISTRIBUTION_PROPERTY = "distribution";
    private final static String VERSION_PROPERTY = "version";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object object) {
        String value;
        if (object instanceof Double) {
            value = Double.toString((Double) object);
        } else {
            value = ((String) object).toUpperCase();
        }
        switch (parameter.getKey()) {
            case ARCHITECTURE_PROPERTY:
                builder.architecture(OsCapability.Architecture.valueOf(value));
                break;
            case TYPE_PROPERTY:
                builder.type(OsCapability.Type.valueOf(value));
                break;
            case DISTRIBUTION_PROPERTY:
                builder.distribution(OsCapability.Distribution.valueOf(value));
                break;
            case VERSION_PROPERTY:
                builder.version(object.toString());
                break;
            default:
                super.handleProperty(node, parameter, builder, object);
                break;
        }
    }

    @Override
    protected Class getBuilderClass() {
        return OsCapabilityBuilder.class;
    }
}
