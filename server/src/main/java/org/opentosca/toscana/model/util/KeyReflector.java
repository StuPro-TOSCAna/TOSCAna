package org.opentosca.toscana.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.parse.graphconverter.MappingEntity;
import org.opentosca.toscana.core.parse.graphconverter.util.NavigationUtil;
import org.opentosca.toscana.core.parse.graphconverter.util.NodeTypeResolver;
import org.opentosca.toscana.model.node.RootNode;

/**
 Util class for detecting real subtypes for wrapping entities.
 Initially developed because of {@link RootNode#getCapabilities()} -
 the corresponding ToscaKey {@link RootNode#CAPABILITIES} does only contain the type information 'Capability'
 (which is abstract), and not the subtypes of the members of the collection. In order to detect the real type,
 reflection had to be used.
 */
public class KeyReflector {

    public static Class detectRealCapabilityType(MappingEntity entity, Class type) {
        MappingEntity nodeEntity = NavigationUtil.getEnclosingNode(entity);
        String nodeTypeIdentifier = nodeEntity.getValue(RootNode.TYPE);
        Class nodeType = NodeTypeResolver.resolve(nodeTypeIdentifier);
        Set<ToscaKey> keys = reflectKeys(nodeType, type);
        return keys.stream()
            .filter(key -> key.getName().equals(entity.getName()))
            .map(key -> key.getType())
            .findFirst().orElseThrow(() -> new IllegalStateException(
                (String.format("No matching wrapper class found for entity '%s'", entity))));
    }

    private static Set<ToscaKey> reflectKeys(Class keyContainer, Class filter) {
        Field[] fields = keyContainer.getFields();
        Map<ToscaKey, Field> tmpKeyMap = new HashMap<>();
        for (Field field : fields) {
            if (field.getType().equals(ToscaKey.class) && Modifier.isStatic(field.getModifiers())) {
                try {
                    ToscaKey toscaKey = (ToscaKey) field.get(null);
                    if (filter.isAssignableFrom(toscaKey.getType())) {
                        tmpKeyMap.put(toscaKey, field);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        Set<ToscaKey> keys = new HashSet<>();
        for (Map.Entry<ToscaKey, Field> entry : tmpKeyMap.entrySet()) {
            for (Map.Entry<ToscaKey, Field> otherEntry : tmpKeyMap.entrySet()) {
                Field field = entry.getValue();
                Field otherField = otherEntry.getValue();
                ToscaKey key = entry.getKey();
                ToscaKey otherKey = otherEntry.getKey();
                if (field != otherField && key.hasSameShape(otherKey)) {
                    // find out which field should not be considered (due to "tosca shadowing")
                    Class clazz = field.getDeclaringClass();
                    Class otherClazz = otherField.getDeclaringClass();
                    if (clazz.isAssignableFrom(otherClazz)) {
                        continue;
                   }
                }
                keys.add(key);
            }
        }
        return keys;
    }
}
