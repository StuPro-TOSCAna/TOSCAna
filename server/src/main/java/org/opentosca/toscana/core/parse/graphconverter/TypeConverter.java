package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.graphconverter.util.IntrinsicFunctionResolver;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.EnumUtils;

public class TypeConverter {

    public static <T> T convert(BaseEntity entity, ToscaKey<T> key) throws AttributeNotSetException {
        if (IntrinsicFunctionResolver.holdsFunction(entity)) {
            BaseEntity resolvedEntity = IntrinsicFunctionResolver.resolveFunction(entity);
            if (resolvedEntity == null) {
                return null;
            } else {
                return convert(resolvedEntity, key);
            }
        } else if (entity instanceof ScalarEntity) {
            ScalarEntity scalarEntity = (ScalarEntity) entity;
            return convertScalarEntity(scalarEntity, key);
        } else if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            return ToscaFactory.wrapEntity(mappingEntity, key.getType());
        } else {
            throw new IllegalStateException(
                String.format("Cannot get value of type '%s' from entity '%s'", key.getType(), entity));
        }
    }

    private static <T> T convertScalarEntity(ScalarEntity scalarEntity, ToscaKey<T> key) {
        String value = scalarEntity.getValue();
        Class targetType = key.getType();
        if (String.class.isAssignableFrom(targetType)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T) Integer.valueOf(value);
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T) Boolean.valueOf(value);
            // TODO handle values besides true/false
        } else if (targetType.isEnum()) {
            Map<String, T> enumMap = EnumUtils.getEnumMap(targetType);
            Optional<T> result = enumMap.entrySet().stream()
                .filter(entry -> value.equalsIgnoreCase(entry.getKey()))
                .map(entry -> entry.getValue())
                .findAny();
            return result.orElseThrow(() -> new NoSuchElementException(
                String.format("No value with name '%s' in enum '%s'", value, targetType.getSimpleName())));
        } else if (OperationVariable.class.isAssignableFrom(targetType)) {
            return (T) new OperationVariable(scalarEntity);
        } else if (SizeUnit.class.isAssignableFrom(targetType)) {
            SizeUnit.Unit fromDefaultUnit = (SizeUnit.Unit) key.getDirectives().get(SizeUnit.FROM);
            SizeUnit.Unit toUnit = (SizeUnit.Unit) key.getDirectives().get(SizeUnit.TO);
            if (fromDefaultUnit == null || toUnit == null) {
                throw new IllegalStateException(
                    "ToscaKey defining a SizeUnit is illegal: No directive set for source and target units");
            }
            return (T) SizeUnit.convert(value, fromDefaultUnit, toUnit);
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
    }
}
