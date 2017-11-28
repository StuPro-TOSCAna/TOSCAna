package org.opentosca.toscana.core.transformation;

import java.io.File;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.slf4j.Logger;

public final class TransformationContext {

    private final Log log;
    private final PluginFileAccess access;
    private final EffectiveModel model;
    private final PropertyInstance properties;

    public TransformationContext(File csarContentDir, File transformationRootDir, Log log,
                                 EffectiveModel model, PropertyInstance properties) {
        this.log = log;
        this.model = model;
        this.properties = properties;
        this.access = new PluginFileAccess(csarContentDir, transformationRootDir, log);
    }

    public EffectiveModel getModel() {
        return model;
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
