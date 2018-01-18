package org.opentosca.toscana.core.parse.graphconverter.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.AttributeNotSetException;
import org.opentosca.toscana.core.parse.graphconverter.BaseEntity;
import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.ScalarEntity;
import org.opentosca.toscana.core.parse.graphconverter.ServiceGraph;
import org.opentosca.toscana.model.Parameter;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;

/**
 Can recognize and resolve TOSCA intrinsic functions.
 */
public class IntrinsicFunctionResolver {

    /**
     @param entity holds function if it's a MappingEntity and has a child entity with its name exactly matching
     one of the intrinsic function names.
     @return true if given entity holds a tosca intrinsic function,  false otherwise
     */
    public static boolean holdsFunction(BaseEntity entity) {
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
     @return returns the resolved target. Returns null if target does not exist and function type allows missing target
     @throws IllegalStateException if call to {@link #holdsFunction(BaseEntity)}
     with same entity as argument comes out as false
     */
    public static BaseEntity resolveFunction(BaseEntity functionHolder) throws AttributeNotSetException {
        if (!holdsFunction(functionHolder)) {
            throw new IllegalStateException(String.format(
                "Given entity '%s' does not hold a function - illegal call to resolveFunction", functionHolder));
        }
        BaseEntity parent = functionHolder.getParent();
        BaseEntity functionTarget = getTarget((MappingEntity) functionHolder);
        if (functionTarget != null) {
            ServiceGraph graph = functionHolder.getGraph();
            // only removing connection - the actual function entities stay in the graph; might be easier for debugging
            graph.removeEdge(parent, functionHolder);
            graph.addConnection(parent, functionTarget, functionHolder.getName());
        }
        return functionTarget;
    }

    private static BaseEntity getTarget(MappingEntity functionHolder) throws AttributeNotSetException {
        BaseEntity functionEntity = functionHolder.getChildren().iterator().next();
        ToscaFunction function = ToscaFunction.getFunction(functionEntity.getName());
        ServiceGraph graph = functionHolder.getGraph();
        switch (function) {
            case GET_INPUT:
                ScalarEntity inputFunctionEntity = (ScalarEntity) functionEntity;
                String inputName = inputFunctionEntity.getValue();
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
                String targetNodeName = ((ScalarEntity) it.next()).getValue();
                BaseEntity targetNode;
                if ("SELF".equals(targetNodeName)) {
                    targetNode = NavigationUtil.getEnclosingNode(functionEntity);
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
                    return noTargetFound(function, functionEntity);
                }
                for (BaseEntity possibleLocation : possibleLocations) {
                    BaseEntity current = findTarget(possibleLocation, it);
                    if (current != null) {
                        return current;
                    }
                }
                return (noTargetFound(function, functionEntity));
            default:
                throw new UnsupportedOperationException(String.format("Function %s not supported yet", function));
        }
    }

    private static BaseEntity noTargetFound(ToscaFunction function, BaseEntity functionEntity) throws AttributeNotSetException {
        String message = String.format("Function at '%s' points to non-existent target", functionEntity.getId());
        if (function == ToscaFunction.GET_ATTRIBUTE) {
            throw new AttributeNotSetException(message);
        } else {
            throw new IllegalStateException(message);
        }
    }

    private static BaseEntity findTarget(BaseEntity current, Iterator<BaseEntity> it) {
        while (it.hasNext()) {
            BaseEntity next = it.next();
            Optional<BaseEntity> child = current.getChild(((ScalarEntity) next).getValue());
            if (child.isPresent()) {
                current = child.get();
            } else {
                return null;
            }
        }
        return current;
    }

    public enum ToscaFunction {
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
