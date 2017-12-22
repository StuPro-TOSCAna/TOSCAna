package org.opentosca.toscana.core.parse.converter.function;

import java.util.Map;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunction.FunctionType;
import org.opentosca.toscana.model.node.RootNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.core.parse.converter.function.ToscaFunction.SourceType;

public class ToscaFunctionFactory {

    private final static Logger logger = LoggerFactory.getLogger(ToscaFunctionFactory.class.getName());
   
    public static ToscaFunction create(ToscaFunctionTemplate template, Map<String, RootNode> nodes) {
        FunctionType type = template.type;
        SourceType sourceType = template.sourceType;
        String sourceName = template.sourceEntity;
        RootNode source = nodes.get(template.sourceEntity);
        String targetName = template.targetEntity;
        RootNode target = nodes.get(targetName);
        String sourceProperty = template.sourceProperty;
        String targetProperty = template.targetProperty;

        if (target == null) {
            throw new IllegalStateException(
                String.format("Target '%s' defined in function '%s' of '%s:%s' does not exist",
                    targetName, type.toString(), sourceName, sourceProperty));
        }

        return new ToscaFunction(type, sourceType, source, sourceProperty, target, targetProperty);
    }
}
