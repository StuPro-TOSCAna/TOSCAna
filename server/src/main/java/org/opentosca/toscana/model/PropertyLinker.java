package org.opentosca.toscana.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.nodedefinition.BaseDefinition;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyLinker {

    private static final Logger logger = LoggerFactory.getLogger(PropertyLinker.class);

    private final ToscaEntity source;
    private final BaseDefinition propertyNameResolver;
    private final Map<String, LinkTarget> linkMap = new HashMap<>();

    public PropertyLinker(ToscaEntity source, BaseDefinition propertyNameResolver) {
        this.source = source;
        this.propertyNameResolver = propertyNameResolver;
    }

    public void link(String toscaSourceFieldName, RootNode target, String toscaTargetFieldName) {
        String javaSourceFieldName = propertyNameResolver.resolve(toscaSourceFieldName);
        String javaTargetFieldName = propertyNameResolver.resolve(toscaTargetFieldName);
        Field targetField = getField(target.getClass(), javaTargetFieldName);
        linkMap.put(javaSourceFieldName, new LinkTarget(target, targetField));
    }

    public <T> T resolveGet(String toscaSourceFieldName) {
        String javaSourceField = propertyNameResolver.resolve(toscaSourceFieldName);
        LinkTarget linkTarget = resolveLinkTarget(javaSourceField);
        Field field = linkTarget.field;
        field.setAccessible(true);
        try {
            return (T) field.get(linkTarget.target);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access field '%s'", field.getName(), e);
            throw new IllegalStateException();
        }
    }

    public <T> void resolveSet(String toscaSourceFieldName, T value) {
        String javaSourceField = propertyNameResolver.resolve(toscaSourceFieldName);
        LinkTarget linkTarget = resolveLinkTarget(javaSourceField);
        Field field = linkTarget.field;
        field.setAccessible(true);
        try {
            field.set(linkTarget.target, value);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access field '%s'", field.getName(), e);
        }
    }

    private LinkTarget resolveLinkTarget(String sourceField) {
        LinkTarget linkTarget = linkMap.get(sourceField);
        if (linkTarget == null) {
            linkTarget = getDefaultLinkTarget(sourceField);
        }
        return linkTarget;
    }

    private LinkTarget getDefaultLinkTarget(String fieldName) {
        Field sourceField = getField(source.getClass(), fieldName);
        return new LinkTarget(source, sourceField);
    }

    private Field getField(Class clazz, String fieldName) {
        Field targetField = FieldUtils.getField(clazz, fieldName, true);
        if (targetField == null) {
            logger.error("Can not resolve property: Class '{}' does not have field '{}'",
                clazz.getSimpleName(), fieldName);
            throw new IllegalStateException();
        }
        return targetField;
    }

    private static class LinkTarget {

        private final Object target;
        private final Field field;

        private LinkTarget(Object target, Field field) {
            this.target = target;
            this.field = field;
        }
    }
}
