package org.opentosca.toscana.core.parse.converter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.model.Connection;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.model.datatype.Port;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.EnumUtils;

public class ScalarTypeConverter {

    /**
     TOSCA keyword 'UNBOUNDED' represents an unlimited positive integer
     */
    public static final String UNBOUNDED = "UNBOUNDED";

    static <T> T convertScalarEntity(ScalarEntity scalarEntity, ToscaKey<T> key, Entity parent) {
        String value = scalarEntity.getValue();
        Class targetType = key.getType();
        if (String.class.isAssignableFrom(targetType)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            Integer number;
            if (UNBOUNDED.equals(value)) {
                number = Integer.MAX_VALUE;
            } else {
                number = Integer.valueOf(value);
            }
            return (T) number;
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T) Boolean.valueOf(value);
            // TODO handle values besides true/false (later, when doing error handling)
        } else if (targetType.isEnum()) {
            Map<String, T> enumMap = EnumUtils.getEnumMap(targetType);
            Optional<T> result = enumMap.entrySet().stream()
                .filter(entry -> value.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny();
            return result.orElseThrow(() -> new NoSuchElementException(
                String.format("No value with name '%s' in enum '%s'", value, targetType.getSimpleName())));
        } else if (OperationVariable.class.isAssignableFrom(targetType)) {
            Connection c = scalarEntity.getGraph().getEdge(parent, scalarEntity);
            String name = null;
            if (c != null) {
                name = c.getKey();
            }
            return (T) new OperationVariable(scalarEntity, name);
        } else if (SizeUnit.class.isAssignableFrom(targetType)) {
            SizeUnit.Unit fromDefaultUnit = (SizeUnit.Unit) key.getDirectives().get(SizeUnit.FROM);
            SizeUnit.Unit toUnit = (SizeUnit.Unit) key.getDirectives().get(SizeUnit.TO);
            if (fromDefaultUnit == null || toUnit == null) {
                throw new IllegalStateException(
                    "ToscaKey defining a SizeUnit is illegal: No directive set for source and target units");
            }
            return (T) SizeUnit.convert(value, fromDefaultUnit, toUnit);
        } else if (Port.class.isAssignableFrom(targetType)) {
            return (T) new Port(Integer.valueOf(value));
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
    }
}
