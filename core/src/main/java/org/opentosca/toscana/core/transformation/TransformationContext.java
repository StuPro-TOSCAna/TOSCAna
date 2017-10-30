package org.opentosca.toscana.core.transformation;

import java.io.File;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.slf4j.Logger;

public final class TransformationContext {

    private final Log log;
    private final PluginFileAccess access;
    private final TServiceTemplate template;
    private final PropertyInstance properties;

    public TransformationContext(File csarContentDir, File transformationRootDir, Log log, TServiceTemplate template, PropertyInstance properties) {
        this.log = log;
        this.template = template;
        this.properties = properties;
        this.access = new PluginFileAccess(csarContentDir, transformationRootDir, log);
    }

    public TServiceTemplate getServiceTemplate() {
        return template;
    }

    public PropertyInstance getProperties() {
        return properties;
    }

    public Logger getLogger(String context) {
        return log.getLogger(context);
    }

    public Logger getLogger(Class clazz) {
        return log.getLogger(clazz);
    }

    public PluginFileAccess getPluginFileAccess() {
        return access;
    }
}
