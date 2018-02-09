package org.opentosca.toscana.core.parse.converter;

import org.opentosca.toscana.core.parse.converter.util.AttributeNotSetException;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.util.ToscaKey;

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
            return ScalarTypeConverter.convertScalarEntity(scalarEntity, key, parent);
        } else if (BaseToscaElement.class.isAssignableFrom(key.getType())) {
            MappingEntity mappingEntity = (MappingEntity) entity;
            return TypeWrapper.wrapEntity(mappingEntity, key.getType());
        } else {
            throw new IllegalStateException(
                String.format("Cannot get value of type '%s' from entity '%s'", key.getType(), entity));
        }
    }
}
