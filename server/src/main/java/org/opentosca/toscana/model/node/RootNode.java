package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.requirement.Dependency;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.visitor.VisitableNode;

import lombok.Builder;
import lombok.Data;

/**
 The base node. Every other node will derive from this class.
 */
@Data
public abstract class RootNode extends DescribableEntity implements VisitableNode {

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
    private final NodeCapability feature = NodeCapability.builder().occurence(Range.EXACTLY_ONCE).build();
    private final StandardLifecycle standardLifecycle;
    /**
     Dependencies are generic requirements that can be used to express timing dependencies between nodes.
     (TOSCA Simple Profile in YAML Version 1.1, p. 23)
     */
    private Set<Dependency> dependencies = new HashSet<>();

    @Builder
    protected RootNode(String nodeName,
                       StandardLifecycle standardLifecycle,
                       String description) {
        super(description);
        this.nodeName = Objects.requireNonNull(nodeName);
        if (nodeName.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.standardLifecycle = (standardLifecycle == null) ? StandardLifecycle.builder().build() : standardLifecycle;
        capabilities.add(this.feature);
        requirements.addAll(this.dependencies);
    }

    /**
     @param nodeName {@link #nodeName}
     */
    public static RootNodeBuilder builder(String nodeName) {
        return new RootNodeBuilder().nodeName(nodeName);
    }

    public static class RootNodeBuilder extends DescribableEntityBuilder {

        private String nodeName;
        private StandardLifecycle standardLifecycle;
        private String description;

        RootNodeBuilder() {
        }

        @Override
        public RootNode build() {
            // should never be called (RootNode is abstract)
            throw new UnsupportedOperationException();
        }

        public RootNodeBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public RootNodeBuilder standardLifecycle(StandardLifecycle standardLifecycle) {
            this.standardLifecycle = standardLifecycle;
            return this;
        }

        @Override
        public RootNodeBuilder description(String description) {
            this.description = description;
            return this;
        }

        public String toString() {
            return "RootNode.RootNodeBuilder(nodeName=" + this.nodeName + ", standardLifecycle=" + this.standardLifecycle + ", description=" + this.description + ")";
        }
    }
}
