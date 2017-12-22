package org.opentosca.toscana.core.parse.converter.function;

import org.opentosca.toscana.model.node.RootNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToscaFunction {

    private final static Logger logger = LoggerFactory.getLogger(ToscaFunction.class.getName());

    public final FunctionType type;
    public final SourceType sourceType;
    public final RootNode source;
    public final String sourceProperty;
    public final RootNode target;
    public final String targetProperty;

    public ToscaFunction(FunctionType type, SourceType sourceType, RootNode source,
                         String sourceProperty, RootNode target, String targetProperty) {
        this.type = type;
        this.sourceType = sourceType;
        this.source = source;
        this.sourceProperty = sourceProperty;
        this.target = target;
        this.targetProperty = targetProperty;
    }

    public static enum FunctionType {
        GET_ATTRIBUTE,
        GET_PROPERTY,
        GET_INPUT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static enum SourceType {
        ENTITY_NAME,
        SELF,
        SOURCE,
        TARGET,
        HOST
    }

    /**
     Applies this function.
     */
    public void apply() {
        switch (this.type) {
            case GET_PROPERTY:
                this.source.link(this.sourceProperty, this.target, this.targetProperty);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
