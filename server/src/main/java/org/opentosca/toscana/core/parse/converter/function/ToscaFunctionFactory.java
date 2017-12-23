package org.opentosca.toscana.core.parse.converter.function;

import java.util.Map;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunction.FunctionType;
import org.opentosca.toscana.model.ToscaEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.core.parse.converter.function.ToscaFunction.SourceType;

public class ToscaFunctionFactory {

    private final static Logger logger = LoggerFactory.getLogger(ToscaFunctionFactory.class.getName());

    public static ToscaFunction create(ToscaFunctionTemplate template, Map<String, ToscaEntity> entities) {
        FunctionType type = template.type;
        SourceType sourceType = template.sourceType;
        ToscaEntity source = entities.get(template.sourceEntity);
        ToscaEntity target = resolveTarget(entities, template);
        String sourceProperty = template.sourceProperty;
        String targetProperty = template.targetProperty;

        if (target == null) {
            throw new IllegalStateException(
                String.format("Target '%s' defined in function '%s' of '%s:%s' does not exist",
                    template.targetEntity, type.toString(), template.sourceEntity, sourceProperty));
        }

        return new ToscaFunction(type, sourceType, source, sourceProperty, target, targetProperty);
    }

    private static ToscaEntity resolveTarget(Map<String, ToscaEntity> entities, ToscaFunctionTemplate template) {
        String resolvedTargetName;
        switch (template.targetEntity) {
            case "SELF":
                resolvedTargetName = template.sourceEntity;
                break;
            case "SOURCE":
            case "TARGET":
            case "HOST":
                throw new UnsupportedOperationException(
                    String.format("The function keyword '%s' is not yet supported by the model converter",
                    template.targetEntity));
            default:
                resolvedTargetName = template.targetEntity;
        }
        return entities.get(resolvedTargetName);
    }
}
