package org.opentosca.toscana.core.parse.graphconverter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeConverter {

    private final Logger logger;
    private final ToscaFactory toscaFactory;
    private final IntrinsicFunctionResolver functionResolver;

    public TypeConverter(Logger logger) {
        this.logger = logger;
        this.toscaFactory = new ToscaFactory(logger);
        this.functionResolver = new IntrinsicFunctionResolver(logger);
    }

    public TypeConverter() {
        this(LoggerFactory.getLogger(TypeConverter.class));
    }

    public <T> T convert(BaseEntity entity, ToscaKey<T> key) {
        if (this.functionResolver.holdsFunction(entity)) {
            BaseEntity resolvedEntity = this.functionResolver.resolveFunction(entity);
            return convert(resolvedEntity, key);
        } else if (entity instanceof ScalarEntity) {
            ScalarEntity scalarEntity = (ScalarEntity) entity;
            return convertScalarEntity(scalarEntity, key);
        } else if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            return toscaFactory.wrapEntity(mappingEntity, key.getType());
        } else {
            throw new IllegalStateException(
                String.format("Cannot get value of type '%s' from entity '%s'", key.getType(), entity));
        }
    }

    private <T> T convertScalarEntity(ScalarEntity scalarEntity, ToscaKey<T> key) {
        String value = scalarEntity.get();
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
