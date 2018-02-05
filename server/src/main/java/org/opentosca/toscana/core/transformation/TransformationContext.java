package org.opentosca.toscana.core.transformation;

import java.io.File;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.slf4j.Logger;

public final class TransformationContext {

    private final Log log;
    private final PluginFileAccess access;
    private final EffectiveModel model;
    private final PropertyInstance properties;
    private final Logger logger;

    public TransformationContext(File csarContentDir, File transformationRootDir, Log log,
                                 EffectiveModel model, PropertyInstance properties) {
        this.log = log;
        this.model = model;
        this.properties = properties;
        this.access = new PluginFileAccess(csarContentDir, transformationRootDir, log);
        this.logger = getLogger(getClass());
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

    /**
     This Method returns true if the plugin should deploy after the transformation is performed.
     False is resturned otherwise.
     <p>
     A Small note to Plugin Developers:
     This method is only intended to be called byt the AbstractLifecycle implementation.
     Plaese DO NOT check if the platform should perform a deployment while performing the Transformation!
     */
    public boolean performDeployment() {
        try {
            String value = properties.get(Platform.DEPLOY_AFTER_TRANSFORMATION_KEY).orElse("false");
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            logger.error("Cannot parse deployment flag in properties", e);
            return false;
        }
    }
}
