package org.opentosca.toscana.model.node;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.capability.NodeCapability;
import org.opentosca.toscana.model.requirement.Dependency;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.datatype.Range;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.visitor.VisitableNode;

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


    /**
     Dependencies are generic requirements that can be used to express timing dependencies between nodes.
     (TOSCA Simple Profile in YAML Version 1.1, p. 23)
     */
    private Set<Dependency> dependencies = new HashSet<>();

    private final StandardLifecycle standardLifecycle;

    protected RootNode(String nodeName,
                       StandardLifecycle standardLifecycle,
                       String description) {
        super(description);
        this.nodeName = Objects.requireNonNull(nodeName);
        if (nodeName.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        this.standardLifecycle = (standardLifecycle == null) ? StandardLifecycle.builder().build() : standardLifecycle;
        capabilities.add(feature);
        requirements.addAll(dependencies);
    }
}
