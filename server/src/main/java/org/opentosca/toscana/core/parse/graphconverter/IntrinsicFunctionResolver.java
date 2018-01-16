package org.opentosca.toscana.core.parse.graphconverter;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.opentosca.toscana.core.parse.graphconverter.util.ToscaStructure;
import org.opentosca.toscana.model.Parameter;

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
            Set<BaseEntity<?>> children = mappingEntity.getChildren();
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
        switch (function) {
            case GET_INPUT:
                ScalarEntity inputFunctionEntity = (ScalarEntity) functionEntity;
                String inputName = inputFunctionEntity.get();
                Optional<BaseEntity<String>> optionalInputEntity = graph.getEntity(ToscaStructure.INPUTS.descend(inputName));
                BaseEntity inputEntity = optionalInputEntity.orElseThrow(() -> new IllegalStateException(
                    String.format("Illegal function at '%s': Input '%s' is referenced but never declared"
                        , functionEntity.getId(), inputName)
                ));
                Optional<BaseEntity<?>> inputValue = inputEntity.getChild(Parameter.VALUE);
                return inputValue.orElseThrow(() -> new IllegalStateException(
                   String.format("Input '%s' is referenced but its value is not set", inputName) 
                ));
            default:
                throw new UnsupportedOperationException(String.format("Function %s not yet supported", function));
        }
    }

    public static enum ToscaFunction {
        GET_INPUT("get_input"),
        GET_PROPERTY("get_property"),
        GET_ATTRIBUTE("get_attribute");

        private final String name;

        ToscaFunction(String name) {
            this.name = name;
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
