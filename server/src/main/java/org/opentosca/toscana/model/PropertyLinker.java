package org.opentosca.toscana.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.model.node.RootNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyLinker {

    private static final Logger logger = LoggerFactory.getLogger(PropertyLinker.class);

    private final RootNode source;
    private final Map<String, LinkTarget> linkMap = new HashMap<>();

    public PropertyLinker(RootNode source) {
        this.source = source;
    }

    public void link(String sourceFieldName, RootNode target, String targetFieldName) {
        Field targetField = getField(target.getClass(), targetFieldName);
        linkMap.put(sourceFieldName, new LinkTarget(target, targetField));
    }

    public <T> T resolveGet(String sourceField) {
        LinkTarget linkTarget = resolveLinkTarget(sourceField);
        Field field = linkTarget.field;
        field.setAccessible(true);
        try {
            return (T) field.get(linkTarget.target);
        } catch (IllegalAccessException e) {
            logger.error("Cannot access field '%s'", field.getName(), e);
            throw new IllegalStateException();
        }
    }

    public <T> void resolveSet(T value, String sourceField) {
        LinkTarget linkTarget = resolveLinkTarget(sourceField);
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
        try {
            Field targetField = clazz.getDeclaredField(fieldName);
            return targetField;
        } catch (NoSuchFieldException e) {
            logger.error("Can not resolve property: Class '{}' does not have field '{}'",
                clazz, fieldName, e);
            throw new IllegalStateException();
        }
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
