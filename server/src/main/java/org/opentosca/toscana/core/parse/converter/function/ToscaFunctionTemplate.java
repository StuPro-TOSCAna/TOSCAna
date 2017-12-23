package org.opentosca.toscana.core.parse.converter.function;

import org.opentosca.toscana.core.parse.converter.function.ToscaFunction.FunctionType;

/**
 POJO which reflects an TOSCA intrinsic function
 */
public class ToscaFunctionTemplate {

    public FunctionType type;
    public ToscaFunction.SourceType sourceType;
    public String sourceEntity;
    public String sourceProperty;
    public String targetEntity;
    public String targetProperty;
}
