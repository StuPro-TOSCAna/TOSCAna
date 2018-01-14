package org.opentosca.toscana.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 Util class for computing the key map of a class.
 */
public class KeyReflector {

    // TODO still needed?
    public static Map<ToscaKey, Field> getToscaKeyFieldMap(Object instance) {
        Map<ToscaKey, Field> tmpKeyMap = new HashMap<>();
        Field[] fields = instance.getClass().getFields();
        for (Field field : fields) {
            if (field.getType() == ToscaKey.class && Modifier.isStatic(field.getModifiers())) {
                try {
                    ToscaKey toscaKey = (ToscaKey) field.get(instance);
                    tmpKeyMap.put(toscaKey, field);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return tmpKeyMap;
    }
}
