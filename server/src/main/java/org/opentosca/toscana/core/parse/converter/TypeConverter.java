package org.opentosca.toscana.core.parse.converter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.core.parse.model.Connection;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.EnumUtils;

@SuppressWarnings("unchecked")
public class TypeConverter {

    public static <T> T convert(Entity entity, ToscaKey<T> key, Entity parent) throws AttributeNotSetException {
        if (IntrinsicFunctionResolver.holdsFunction(entity)) {
            Entity resolvedEntity = IntrinsicFunctionResolver.resolveFunction(entity);
            if (resolvedEntity == null) {
                return null;
            } else {
                return convert(resolvedEntity, key, parent);
            }
        } else if (entity instanceof ScalarEntity) {
            ScalarEntity scalarEntity = (ScalarEntity) entity;
            return convertScalarEntity(scalarEntity, key, parent);
        } else if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            return TypeWrapper.wrapEntity(mappingEntity, key.getType());
        } else {
            throw new IllegalStateException(
                String.format("Cannot get value of type '%s' from entity '%s'", key.getType(), entity));
        }
    }

    private static <T> T convertScalarEntity(ScalarEntity scalarEntity, ToscaKey<T> key, Entity parent) {
        String value = scalarEntity.getValue();
        Class targetType = key.getType();
        if (String.class.isAssignableFrom(targetType)) {
            return (T) value;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T) Integer.valueOf(value);
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
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
    }
}
