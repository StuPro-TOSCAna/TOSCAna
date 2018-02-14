package org.opentosca.toscana.model.node.custom;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.capability.ContainerCapability;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.util.ToscaKey;
import org.opentosca.toscana.model.visitor.NodeVisitor;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 Represents a java runtime installation.
 <p>
 Note: The component_version shall be specified as '1.7' (developer version), not '7' (product version).
 If the component_version is omitted, orchestrators shall use the latest available version.
 */
@EqualsAndHashCode
@ToString
public class JavaRuntime extends SoftwareComponent {

    public static ToscaKey<ContainerCapability> JRE_HOST = new ToscaKey<>(CAPABILITIES, "host")
        .type(ContainerCapability.class);

    public JavaRuntime(MappingEntity mappingEntity) {
        super(mappingEntity);
        ContainerCapability jreCapability = new ContainerCapability(getChildEntity(JRE_HOST));
        jreCapability.getValidSourceTypes().clear();
        jreCapability.getValidSourceTypes().add(JavaApplication.class);
        setDefault(JRE_HOST, jreCapability);
    }

    /**
     @return {@link #JRE_HOST}
     */
    public ContainerCapability getJreHost() {
        return get(JRE_HOST);
    }

    /**
     Sets {@link #JRE_HOST}
     */
    public JavaRuntime setJreHost(ContainerCapability jreHost) {
        set(JRE_HOST, jreHost);
        return this;
    }

    @Override
    public void accept(NodeVisitor v) {
        v.visit(this);
    }
}
