package org.opentosca.toscana.core.parse.converter.function;

import java.util.Map;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunction.FunctionType;
import org.opentosca.toscana.model.ToscaEntity;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.OperationVariable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.core.parse.converter.function.ToscaFunction.SourceType;

public class ToscaFunctionFactory {

    private final static Logger logger = LoggerFactory.getLogger(ToscaFunctionFactory.class.getName());

    public static ToscaFunction create(ToscaFunctionTemplate template, Map<String, ToscaEntity> entities) {
        FunctionType type = template.type;
        SourceType sourceType = template.sourceType;
        ToscaEntity source = entities.get(template.sourceEntity);
        ToscaEntity target = entities.get(template.targetEntity);
        String sourceProperty = template.sourceProperty;
        String targetProperty = template.targetProperty;

        if (target == null) {
            throw new IllegalStateException(
                String.format("Target '%s' defined in function '%s' of '%s:%s' does not exist",
                    template.targetEntity, type.toString(), template.sourceEntity, sourceProperty));
        }

        return new ToscaFunction(type, sourceType, source, sourceProperty, target, targetProperty);
    }
}
