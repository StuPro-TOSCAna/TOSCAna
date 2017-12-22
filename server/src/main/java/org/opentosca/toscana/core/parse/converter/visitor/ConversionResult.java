package org.opentosca.toscana.core.parse.converter.visitor;

import java.util.Set;

import org.opentosca.toscana.core.parse.converter.RequirementConversion;
import org.opentosca.toscana.core.parse.converter.function.ToscaFunctionTemplate;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;

public class ConversionResult<R> extends AbstractResult<ConversionResult<R>> {

    private final R result;
    private final Set<RequirementConversion> requirementConversions;
    private final Set<ToscaFunctionTemplate> functions;

    public ConversionResult(R result, Context context) {
        this.result = result;
        if (context != null) {
            this.requirementConversions = context.getRequirementConversions();
            this.functions = context.getFunctions();
        } else {
            this.requirementConversions = null;
            this.functions = null;
        }
    }

    public ConversionResult(R result) {
        this(result, null);
    }

    @Override
    public ConversionResult add(ConversionResult o) {
        return this;
    }

    public R getResult() {
        return result;
    }

    public Set<RequirementConversion> getRequirementConversions() {
        return requirementConversions;
    }

    public Set<ToscaFunctionTemplate> getFunctions() {
        return functions;
    }
}
