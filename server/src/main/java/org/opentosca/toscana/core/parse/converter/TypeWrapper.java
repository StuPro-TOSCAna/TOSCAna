package org.opentosca.toscana.core.parse.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.opentosca.toscana.core.parse.converter.util.TypeResolver;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.Entity;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.node.RootNode;

import org.apache.commons.lang.reflect.ConstructorUtils;

import static org.opentosca.toscana.model.node.RootNode.TYPE;

public class TypeWrapper {

    public static Map<String, RootNode> wrapNodes(ServiceGraph graph) {
        Map<String, RootNode> nodes = new HashMap<>();
        Iterator<Entity> it = graph.iterator(ToscaStructure.NODE_TEMPLATES);
        while (it.hasNext()) {
            RootNode node = wrapTypedElement((MappingEntity) it.next());
            nodes.put(node.getEntityName(), node);
        }
        return nodes;
    }

    public static <T> T wrapTypedElement(MappingEntity entity) {
        String typeString = entity.getValue(TYPE);
        Class type = TypeResolver.resolve(typeString);
        return wrap(entity, type);
    }

    public static <T> T wrapEntity(MappingEntity entity, Class type) {
        if (entity == null) {
            return null;
        }
        if (entity.getChild(BaseToscaElement.TYPE.name).isPresent()) {
            try {
                return wrapTypedElement(entity);
            } catch (UnsupportedOperationException e) {
                return wrap(entity, type);
            }
        } else {
            return wrap(entity, type);
        }
    }

    private static <T> T wrap(MappingEntity entity, Class type) {
        try {
            if (Modifier.isAbstract(type.getModifiers())) {
                type = KeyReflector.detectRealSubtype(entity, type);
            }
            T element = (T) ConstructorUtils.invokeConstructor(type, entity);
            return element;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(String.format("Failed to wrap up entity '%s' in type '%s'", entity, type), e);
        }
    }
}
