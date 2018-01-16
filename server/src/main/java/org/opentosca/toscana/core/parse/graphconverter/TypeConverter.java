package org.opentosca.toscana.core.parse.graphconverter;

import org.opentosca.toscana.model.BaseToscaElement;
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
        if (entity instanceof ScalarEntity) {
            ScalarEntity scalarEntity = (ScalarEntity) entity;
            return convert(scalarEntity.get(), key.getType());
        } else if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            return toscaFactory.wrapEntity(mappingEntity, key.getType());
        } else if (this.functionResolver.holdsFunction(entity)) {
            BaseEntity resolvedEntity = this.functionResolver.resolveFunction(entity);
            return convert(resolvedEntity, key);
        } else {
            throw new IllegalStateException(
                String.format("Cannot get value of type '%s' from entity '%s'", key.getType(), entity));
        }
    }

    private <T> T convert(String string, Class targetType) {
        if (String.class.isAssignableFrom(targetType)) {
            return (T) string;
        } else if (Integer.class.isAssignableFrom(targetType)) {
            return (T) Integer.valueOf(string);
        } else if (Boolean.class.isAssignableFrom(targetType)) {
            return (T) Boolean.valueOf(string);
            // TODO handle values besides true/false
        } else if (targetType.isEnum()) {
            T result = (T) EnumUtils.getEnum(targetType, string);
            // TODO handle wrong values
            return result;
        } else {
            throw new UnsupportedOperationException(String.format(
                "Cannot convert value of type %s: currently unsupported", targetType.getSimpleName()));
        }
    }
}
