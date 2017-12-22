package org.opentosca.toscana.core.parse.converter.util;

import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunction.FunctionType;
import org.opentosca.toscana.core.parse.converter.function.ToscaFunctionTemplate;
import org.opentosca.toscana.core.parse.converter.visitor.Context;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterConverter {

    private final static Logger logger = LoggerFactory.getLogger(ParameterConverter.class.getName());

    public ToscaFunctionTemplate convert(String key, TPropertyAssignment propertyAssignment, Context context) {
        if (propertyAssignment.getValue() instanceof Map) {
            return handleFunction(key, (Map<String, Object>) propertyAssignment.getValue(), context);
        } else {
            throw new IllegalStateException();
        }
    }

    private ToscaFunctionTemplate handleFunction(String sourceProperty, Map<String, Object> map, Context context) {
        ToscaFunctionTemplate function = new ToscaFunctionTemplate();
        function.sourceProperty = sourceProperty;
        function.sourceEntity = context.getNodeName();
        if (map.size() == 1) {
            if (map.containsKey(FunctionType.GET_INPUT.toString())) {
                function.type = FunctionType.GET_INPUT;
                String linkedInput = (String) map.get(FunctionType.GET_INPUT.toString());
                function.targetEntity = linkedInput;
            } else if (map.containsKey(FunctionType.GET_ATTRIBUTE.toString())) {
                // TODO

            } else if (map.containsKey(FunctionType.GET_PROPERTY.toString())) {
                function.type = FunctionType.GET_PROPERTY;
                List<String> targetDefinition = (List<String>) map.get(FunctionType.GET_PROPERTY.toString());
                function.targetEntity = targetDefinition.get(0);
                function.targetProperty = targetDefinition.get(1);
            } else
                throw new UnsupportedOperationException(
                    String.format("TOSCA function type '%s' is not supported",
                        map.entrySet().iterator().next()));
        } else {
            throw new IllegalArgumentException();
        }
        return function;
    }
}
