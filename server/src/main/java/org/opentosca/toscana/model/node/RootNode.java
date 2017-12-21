package org.opentosca.toscana.model.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.PropertyLinker;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.nodedefinition.AbstractDefinition;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Dependency;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.VisitableNode;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 The base node. Every other node will derive from this class.
 */
@EqualsAndHashCode
@ToString
public abstract class RootNode extends DescribableEntity implements VisitableNode {

    protected static final Map<String, String> FIELD_MAP = new HashMap<>();
    private final PropertyLinker propertyLinker;

    protected final Set<Requirement> requirements = new HashSet<>();
    protected final Set<Capability> capabilities = new HashSet<>();

    /**
     The symbolic name (node_type_name) of this node.
     (TOSCA Simple Profile in YAML Version 1.1, p. 93)
     */
    private final String nodeName;

    /**
     Every node has the capability of a node.
     */
    private final NodeCapability feature = NodeCapability.builder().occurrence(Range.EXACTLY_ONCE).build();
    private final StandardLifecycle standardLifecycle;
    /**
     Dependencies are generic requirements that can be used to express timing dependencies between nodes.
     (TOSCA Simple Profile in YAML Version 1.1, p. 23)
     */
    private Set<Dependency> dependencies = new HashSet<>();

    @Builder
    protected RootNode(String nodeName,
                       StandardLifecycle standardLifecycle,
                       Set<Requirement> requirements,
                       Set<Capability> capabilities,
                       String description) {
        super(description);
        this.propertyLinker = new PropertyLinker(this, getDefinition());
        this.nodeName = Objects.requireNonNull(nodeName);
        if (nodeName.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.standardLifecycle = (standardLifecycle == null) ? StandardLifecycle.builder().build() : standardLifecycle;
        this.capabilities.add(this.feature);
        this.requirements.addAll(this.dependencies);
        if (capabilities != null) this.capabilities.addAll(capabilities);
        if (requirements != null) this.requirements.addAll(requirements);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static RootNodeBuilder builder(String nodeName) {
        return new RootNodeBuilder().nodeName(nodeName);
    }

    /**
     Establishes a link between this instance's property (defined by sourceField) and the targets targetField.
     In other words, the linked property now behaves like a symbolic link.
     */
    public void link(String toscaSourceFieldName, RootNode target, String toscaTargetFieldName) {
        propertyLinker.link(toscaSourceFieldName, target, toscaTargetFieldName);
    }

    protected <T> T get(String propertyName) {
        return propertyLinker.resolveGet(propertyName);
    }

    protected <T> void set(String propertyName, T value) {
        propertyLinker.resolveSet(propertyName, value);
    }

    protected abstract AbstractDefinition getDefinition();

    public static class RootNodeBuilder extends DescribableEntityBuilder {

        protected Set<Requirement> requirements = new HashSet<>();
        protected Set<Capability> capabilities = new HashSet<>();

        protected RootNodeBuilder() {
        }

        public RootNodeBuilder requirement(Requirement requirement) {
            requirements.add(requirement);
            return this;
        }

        public RootNodeBuilder capability(Capability capability) {
            capabilities.add(capability);
            return this;
        }

        @Override
        public RootNode build() {
            // should never be called (RootNode is abstract)
            throw new UnsupportedOperationException();
        }
    }
}
