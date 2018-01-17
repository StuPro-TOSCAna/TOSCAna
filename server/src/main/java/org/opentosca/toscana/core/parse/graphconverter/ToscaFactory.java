package org.opentosca.toscana.core.parse.graphconverter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.opentosca.toscana.core.parse.graphconverter.util.NodeTypeResolver;
import org.opentosca.toscana.core.parse.graphconverter.util.ToscaStructure;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang.reflect.ConstructorUtils;

public class ToscaFactory {

    // todo move to rootnode or similar ?
    private static final ToscaKey<String> TYPE = new ToscaKey<>("type");

    public static Map<String, RootNode> wrapNodes(ServiceModel serviceModel) {
        Map<String, RootNode> nodes = new HashMap<>();
        Iterator<BaseEntity> it = serviceModel.iterator(ToscaStructure.NODE_TEMPLATES);
        while (it.hasNext()) {
            RootNode node = wrapNode((MappingEntity) it.next());
            nodes.put(node.getEntityName(), node);
        }
        return nodes;
    }

    public static <T> T wrapNode(MappingEntity nodeEntity) {
        String typeString = nodeEntity.getValue(TYPE);
        Class nodeType = NodeTypeResolver.resolve(typeString);
        if (nodeType != null) {
            return wrap(nodeEntity, nodeType);
        } else {
            throw new UnsupportedOperationException(String.format("NodeType '%s' is not supported", typeString));
        }
    }

    public static <T> T wrapEntity(MappingEntity entity, Class type) {
        if (entity == null) {
            return null;
        }
        if (RootNode.class.isAssignableFrom(type)) {
            return wrapNode(entity);
        } else {
            return wrap(entity, type);
        }
    }

    private static <T> T wrap(MappingEntity entity, Class type) {
        try {
            T node = (T) ConstructorUtils.invokeConstructor(type, entity);
            return node;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new IllegalStateException(String.format("Failed to wrap up entity '%s' in type '%s'", entity, type, e));
        }
    }
}
