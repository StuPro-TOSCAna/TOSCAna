package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 See TOSCA Specification V1.1 ch. 2.1.1; 2.14
 <p>
 Serves either as input or output variable for an operation.
 */
@EqualsAndHashCode
@ToString
public class OperationVariable extends BaseToscaElement {

    public final static ToscaKey<String> VALUE = new ToscaKey("value");

    public OperationVariable(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #VALUE}
     */
    public Optional<String> getValue() {
        return Optional.ofNullable(get(VALUE));
    }

    public void setValue(String value) {
        set(VALUE, value);
    }

    public String getKey() {
        return getEntityName();
    }
}
