package org.opentosca.toscana.core.parse.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.parse.converter.util.NavigationUtil;
import org.opentosca.toscana.core.parse.converter.util.TypeResolver;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.BaseToscaElement;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;
import org.opentosca.toscana.model.util.RequirementKey;
import org.opentosca.toscana.model.util.ToscaKey;

/**
 Util class for detecting real subtypes for wrapping entities.
 Initially developed because of {@link RootNode#getCapabilities()} -
 the corresponding ToscaKey {@link RootNode#CAPABILITIES} does only contain the type information 'Capability'
 (which is abstract), and not actual subtypes of the members in the collection.
 In order to detect the real type, reflection had to be used.
 <p>
 The code is quite abstract and hard to read, so here is a brief summary of the applied algorithm:<br>
 1. Detects the class type of the enclosing node of given entity.<br>
 2. Find all declared ToscaKeys of this class type (using reflection), including inherited ToscaKeys.<br>
 3. Remove all ToscaKeys from list whose type is not a subtype of given base type.<br>
 4. Remove all ToscaKeys from list whose name does not match the name of given entity.<br>
 5. Remove 'shadowed' ToscaKeys from found keys. A ToscaKey is shadowed in case a key with the same name
 is declared lower in the inheritance hierarchy.
 */
public class KeyReflector {

    /**
     Returns the real class belonging to given entity.

     @param type the (possibly abstract) parent class
     @return the real class, is a subclass of given type
     */
    public static Class detectRealSubtype(MappingEntity entity, Class type) {
        MappingEntity nodeEntity = NavigationUtil.getEnclosingNode(entity);
        String nodeTypeIdentifier = nodeEntity.getValue(BaseToscaElement.TYPE);
        Class nodeType = TypeResolver.resolve(nodeTypeIdentifier);
        String filterName;
        Class toscaKeyType;
        ToscaKey result;
        if (Capability.class.isAssignableFrom(type)) {
            filterName = entity.getName();
            toscaKeyType = type;
            result = detectRealKey(entity, nodeType, toscaKeyType, filterName);
            return result.getType();
        } else if (RootRelationship.class.isAssignableFrom(type)) {
            filterName = entity.getParent().get().getName();
            toscaKeyType = Requirement.class;
            result = detectRealKey(entity, nodeType, toscaKeyType, filterName);
            return (Class) result.getDirectives().get(RequirementKey.RELATIONSHIP);
        } else {
            throw new IllegalStateException(String.format("Conversion of given abstract type %s not yet supported", type));
        }
    }

    private static ToscaKey detectRealKey(MappingEntity entity, Class keyContainer, Class keyType, String filterName) {
        Set<ToscaKey<?>> keys = reflectKeys(keyContainer, keyType);
        return keys.stream()
            .filter(key -> key.getName().equals(filterName))
            .findFirst().orElseThrow(() -> new IllegalStateException(
                (String.format("No matching wrapper class found for entity '%s'", entity))));
    }

    private static Set<ToscaKey<?>> reflectKeys(Class keyContainer, Class<?> filter) {
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

        Set<ToscaKey<?>> keys = new HashSet<>();
        for (Map.Entry<ToscaKey, Field> entry : tmpKeyMap.entrySet()) {
            for (Map.Entry<ToscaKey, Field> otherEntry : tmpKeyMap.entrySet()) {
                Field field = entry.getValue();
                Field otherField = otherEntry.getValue();
                ToscaKey<?> key = entry.getKey();
                ToscaKey<?> otherKey = otherEntry.getKey();
                if (field != otherField && key.hasSameShape(otherKey)) {
                    // find out which field should not be considered (due to "tosca shadowing")
                    // shadowing works roughly the same in tosca as in java, but for static fields it doesn't work,
                    // hence this workaround
                    Class<?> clazz = field.getDeclaringClass();
                    Class<?> otherClazz = otherField.getDeclaringClass();
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
