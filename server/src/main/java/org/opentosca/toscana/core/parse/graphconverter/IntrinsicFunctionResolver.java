package org.opentosca.toscana.core.parse.graphconverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.util.ToscaStructure;
import org.opentosca.toscana.model.Parameter;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;

import org.slf4j.Logger;

/**
 Can recognize and resolve TOSCA intrinsic functions.
 */
public class IntrinsicFunctionResolver {

    private final Logger logger;

    public IntrinsicFunctionResolver(Logger logger) {
        this.logger = logger;
    }

    /**
     @param entity holds function if it's a MappingEntity and has a child entity with its name exactly matching
     one of the intrinsic function names.
     @return true if given entity holds a tosca intrinsic function,  false otherwise
     */
    public boolean holdsFunction(BaseEntity entity) {
        if (entity instanceof MappingEntity) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            Collection<BaseEntity> children = mappingEntity.getChildren();
            if (children.size() == 1) {
                BaseEntity child = children.iterator().next();
                for (ToscaFunction toscaFunction : ToscaFunction.values()) {
                    if (toscaFunction.name.equals(child.getName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     @param functionHolder the outcome of a call to {@link #holdsFunction(BaseEntity)} with this entity must be true
     @throws IllegalStateException if call to {@link #holdsFunction(BaseEntity)}
     with same entity as argument comes out as false
     */
    public BaseEntity resolveFunction(BaseEntity functionHolder) {
        if (!holdsFunction(functionHolder)) {
            throw new IllegalStateException(String.format(
                "Given entity '%s' does not hold a function - illegal call to resolveFunction", functionHolder));
        }
        BaseEntity parent = functionHolder.getParent();
        BaseEntity functionTarget = getTarget((MappingEntity) functionHolder);
        ServiceGraph graph = functionHolder.getGraph();
        // only removing connection - the actual function entities stay in the graph; might be easier for debugging
        graph.removeEdge(parent, functionHolder);
        graph.addConnection(parent, functionTarget, functionHolder.getName());
        return functionTarget;
    }

    private BaseEntity getTarget(MappingEntity functionHolder) {
        BaseEntity functionEntity = functionHolder.getChildren().iterator().next();
        ToscaFunction function = ToscaFunction.getFunction(functionEntity.getName());
        ServiceGraph graph = functionHolder.getGraph();
        IllegalStateException illegalTargetException = new IllegalStateException(String.format("Function at '%s' points to non-existent target",
            functionEntity.getId()));
        switch (function) {
            case GET_INPUT:
                ScalarEntity inputFunctionEntity = (ScalarEntity) functionEntity;
                String inputName = inputFunctionEntity.get();
                BaseEntity inputEntity = graph.getEntityOrThrow(ToscaStructure.INPUTS.descend(inputName));
                Optional<BaseEntity> inputValue = inputEntity.getChild(Parameter.VALUE);
                return inputValue.orElseThrow(() -> new IllegalStateException(
                    String.format("Input '%s' is referenced but its value is not set", inputName)
                ));
            case GET_PROPERTY:
            case GET_ATTRIBUTE:
                ToscaKey location = function.location;
                // todo refactor
                Collection<BaseEntity> targetAddress = functionEntity.getChildren();
                if (targetAddress.size() < 2) {
                    throw new IllegalStateException(
                        String.format("Illegal amount of parameters for function at '%s'", functionEntity.getId()));
                }
                Iterator<BaseEntity> it = targetAddress.iterator();
                String targetNodeName = ((ScalarEntity) it.next()).get();
                BaseEntity targetNode;
                if ("SELF".equals(targetNodeName)) {
                    targetNode = resolveSelfKeyword(functionEntity);
                } else {
                    targetNode = graph.getEntityOrThrow(ToscaStructure.NODE_TEMPLATES.descend(targetNodeName));
                }
                List<BaseEntity> possibleLocations = new ArrayList<>();
                ToscaKey capabilityLocation = new ToscaKey(RootNode.CAPABILITIES, location.name);
                ToscaKey requirementLocation = new ToscaKey(RootNode.REQUIREMENTS, location.name);
                for (ToscaKey key : new ToscaKey[]{location, capabilityLocation, requirementLocation}) {
                    targetNode.getChild(key).ifPresent(e -> possibleLocations.add(e));
                }
                if (possibleLocations.isEmpty()) {
                    throw illegalTargetException;
                }
                for (BaseEntity possibleLocation : possibleLocations) {
                    BaseEntity current = findTarget(possibleLocation, it);
                    if (current != null) {
                        return current;
                    }
                }
                throw illegalTargetException;
            default:
                throw new UnsupportedOperationException(String.format("Function %s not supported yet", function));
        }
    }

    /**
     @return the node entity referenced by 'SELF'
     */
    private BaseEntity resolveSelfKeyword(BaseEntity current) {
        BaseEntity parent = current;
        do {
            current = parent;
            parent = current.getParent();
        } while (!ToscaStructure.NODE_TEMPLATES.equals(parent.getId()));

        return current;
    }

    private BaseEntity findTarget(BaseEntity current, Iterator<BaseEntity> it) {
        while (it.hasNext()) {
            BaseEntity next = it.next();
            Optional<BaseEntity> child = current.getChild(((ScalarEntity) next).get());
            if (child.isPresent()) {
                current = child.get();
            } else {
                return null;
            }
        }
        return current;
    }

    public static enum ToscaFunction {
        GET_INPUT("get_input"),
        GET_PROPERTY("get_property", RootNode.PROPERTIES),
        GET_ATTRIBUTE("get_attribute", RootNode.ATTRIBUTES);

        private final String name;
        private ToscaKey location;

        ToscaFunction(String name) {
            this.name = name;
        }

        ToscaFunction(String name, ToscaKey location) {
            this(name);
            this.location = location;
        }

        public static ToscaFunction getFunction(String name) {
            for (ToscaFunction value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            throw new NoSuchElementException(String.format("Illegal tosca function name: '%s'", name));
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
