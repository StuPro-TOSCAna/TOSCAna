package org.opentosca.toscana.core.transformation;

import java.io.File;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.slf4j.Logger;

public final class TransformationContext {
    private final Transformation transformation;
    private final PluginFileAccess access;

    public TransformationContext(Transformation transformation, File csarContentDir, File transformationRootDir) {
        this.transformation = transformation;
        this.access = new PluginFileAccess(transformation, csarContentDir, transformationRootDir);
    }

    public TServiceTemplate getServiceTemplate() {
        return transformation.getCsar().getTemplate();
    }

    public PropertyInstance getProperties() {
        return transformation.getProperties();
    }

    public Logger getLogger(String context) {
        return transformation.getLog().getLogger(context);
    }

    public Logger getLogger(Class clazz) {
        return transformation.getLog().getLogger(clazz);
    }

    public PluginFileAccess getPluginFileAccess() {
        return access;
    }
}
