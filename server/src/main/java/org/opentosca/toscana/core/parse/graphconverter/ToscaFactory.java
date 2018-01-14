package org.opentosca.toscana.core.parse.graphconverter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.opentosca.toscana.core.parse.graphconverter.util.NodeTypeResolver;
import org.opentosca.toscana.core.parse.graphconverter.util.ToscaStructure;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.util.ToscaKey;

import org.apache.commons.lang.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToscaFactory {

    // todo move to rootnode or similar ?
    private static final ToscaKey<String> TYPE = new ToscaKey<>("type");
    private final Logger logger;

    public ToscaFactory(Logger logger) {
        this.logger = logger;
    }

    public ToscaFactory() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public Map<String, RootNode> wrapNodes(ServiceModel serviceModel) {
        Map<String, RootNode> nodes = new HashMap<>();
        Iterator<MappingEntity> it = serviceModel.iterator(ToscaStructure.NODE_TEMPLATES);
        while (it.hasNext()) {
            RootNode node = wrapNode(it.next());
            nodes.put(node.getEntityName(), node);
        }
        return nodes;
    }

    public <T> T wrapNode(MappingEntity nodeEntity) {
        String typeString = nodeEntity.get(TYPE);
        Class nodeType = NodeTypeResolver.resolve(typeString);
        if (nodeType != null) {
            return wrap(nodeEntity, nodeType);
        } else {
            logger.error(String.format("NodeType '%s' is not supported", typeString));
            throw new UnsupportedOperationException();
        }
    }

    public <T> T wrapEntity(MappingEntity entity, Class type) {
        if (entity == null) {
            return null;
        }
        if (RootNode.class.isAssignableFrom(type)) {
            return wrapNode(entity);
        } else {
            return wrap(entity, type);
        }
    }

    private <T> T wrap(MappingEntity entity, Class type) {
        try {
            T node = (T) ConstructorUtils.invokeConstructor(type, entity);
            return node;
        } catch (Exception e) {
            logger.error(String.format("Failed to wrap up entity '%s' in type '%s'", entity, type));
            throw new IllegalStateException(e);
        }
    }
}
