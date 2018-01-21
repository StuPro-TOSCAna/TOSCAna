package org.opentosca.toscana.core.parse.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.core.parse.converter.util.NavigationUtil;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.Parameter;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;

/**
 Can recognize and resolve TOSCA intrinsic functions.
 */
public class IntrinsicFunctionResolver {

    /**
     @param entity holds function if entity is a MappingEntity and has a child entity with its name exactly matching
     one of the supported intrinsic function names.
     @return true if given entity holds a tosca intrinsic function,  false otherwise
     */
    public static boolean holdsFunction(Entity entity) {
        if (entity instanceof MappingEntity) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            Collection<Entity> children = mappingEntity.getChildren();
            if (children.size() == 1) {
                Entity child = children.iterator().next();
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
     @param functionHolder the outcome of a call to {@link #holdsFunction(Entity)} with this entity must be true
     @return returns the resolved target. Returns null if target does not exist and function type allows missing target
     @throws IllegalStateException if call to {@link #holdsFunction(Entity)}
     with same entity as argument comes out as false
     */
    public static Entity resolveFunction(Entity functionHolder) throws AttributeNotSetException {
        if (!holdsFunction(functionHolder)) {
            throw new IllegalStateException(String.format(
                "Given entity '%s' does not hold a function - illegal call to resolveFunction", functionHolder));
        }
        Entity parent = functionHolder.getParent();
        Entity functionTarget = getTarget((MappingEntity) functionHolder);
        if (functionTarget != null) {
            ServiceGraph graph = functionHolder.getGraph();
            // only removing connection - the actual function entities stay in the graph; might be easier for debugging
            graph.removeEdge(parent, functionHolder);
            graph.addConnection(parent, functionTarget, functionHolder.getName());
        }
        return functionTarget;
    }

    private static Entity getTarget(MappingEntity functionHolder) throws AttributeNotSetException {
        Entity functionEntity = functionHolder.getChildren().iterator().next();
        ToscaFunction function = ToscaFunction.getFunction(functionEntity.getName());
        switch (function) {
            case GET_INPUT:
                return getInputTarget(functionEntity);
            case GET_PROPERTY:
            case GET_ATTRIBUTE:
                return getPropertyOrAtributeTarget(functionEntity, function);
            default:
                throw new UnsupportedOperationException(String.format("Function %s not supported yet", function));
        }
    }

    private static Entity getInputTarget(Entity functionEntity) {
        ServiceGraph graph = functionEntity.getGraph();
        ScalarEntity inputFunctionEntity = (ScalarEntity) functionEntity;
        String inputName = inputFunctionEntity.getValue();
        Entity inputEntity = graph.getEntityOrThrow(ToscaStructure.INPUTS.descend(inputName));
        Optional<Entity> inputValue = inputEntity.getChild(Parameter.VALUE);
        return inputValue.orElseThrow(() -> new IllegalStateException(
            String.format("Input '%s' is referenced but its value is not set", inputName)
        ));
    }

    private static Entity getPropertyOrAtributeTarget(Entity functionEntity, ToscaFunction function) throws AttributeNotSetException {
        Collection<Entity> targetAddress = functionEntity.getChildren();
        if (targetAddress.size() < 2) {
            throw new IllegalStateException(
                String.format("Illegal amount of parameters for function at '%s'", functionEntity.getId()));
        }
        Iterator<Entity> it = targetAddress.iterator();
        Entity targetNode = getTargetNodeEntity(it.next(), functionEntity);
        List<Entity> possibleLocations = getPossibleTargetLocations(function.location, targetNode);
        if (possibleLocations.isEmpty()) {
            return noTargetFound(function, functionEntity);
        }
        for (Entity possibleLocation : possibleLocations) {
            Entity current = findTarget(possibleLocation, it);
            if (current != null) {
                return current;
            }
        }
        return (noTargetFound(function, functionEntity));
    }

    private static Entity getTargetNodeEntity(Entity targetNodeNameEntity, Entity functionEntity) {
        ServiceGraph graph = targetNodeNameEntity.getGraph();
        String targetNodeName = ((ScalarEntity) targetNodeNameEntity).getValue();
        if ("SELF".equals(targetNodeName)) {
            return NavigationUtil.getEnclosingNode(functionEntity);
        } else {
            return graph.getEntityOrThrow(ToscaStructure.NODE_TEMPLATES.descend(targetNodeName));
        }
    }

    private static List<Entity> getPossibleTargetLocations(ToscaKey location, Entity targetNode) {
        List<Entity> possibleLocations = new ArrayList<>();
        ToscaKey capabilityLocation = new ToscaKey<>(RootNode.CAPABILITIES, location.name);
        ToscaKey requirementLocation = new ToscaKey<>(RootNode.REQUIREMENTS, location.name);
        for (ToscaKey key : new ToscaKey[]{location, capabilityLocation, requirementLocation}) {
            targetNode.getChild(key).ifPresent(possibleLocations::add);
        }
        return possibleLocations;
    }

    private static Entity noTargetFound(ToscaFunction function, Entity functionEntity) throws AttributeNotSetException {
        String message = String.format("Function at '%s' points to non-existent target", functionEntity.getId());
        if (function == ToscaFunction.GET_ATTRIBUTE) {
            throw new AttributeNotSetException(message);
        } else {
            throw new IllegalStateException(message);
        }
    }

    private static Entity findTarget(Entity current, Iterator<Entity> it) {
        while (it.hasNext()) {
            Entity next = it.next();
            Optional<Entity> child = current.getChild(((ScalarEntity) next).getValue());
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
