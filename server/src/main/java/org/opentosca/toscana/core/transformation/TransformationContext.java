package org.opentosca.toscana.core.transformation;

import java.io.File;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.NoSuchPropertyException;
import org.opentosca.toscana.core.transformation.properties.PropertyInstance;
import org.opentosca.toscana.model.EffectiveModel;

import org.slf4j.Logger;

public final class TransformationContext {

    private final Logger logger;

    private final Transformation transformation;
    private final PluginFileAccess access;

    public TransformationContext(Transformation transformation, File transformationRootDir) {
        this.logger = transformation.getLog().getLogger(getClass());
        this.transformation = transformation;
        this.access = new PluginFileAccess(transformation.getCsar().getContentDir(),
            transformationRootDir, transformation.getLog());
    }

    public EffectiveModel getModel() {
        return transformation.getModel();
    }

    public PropertyInstance getInputs() {
        return transformation.getInputs();
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

    /**
     This method returns true if the plugin should deploy after the transformation is performed.
     False is returned otherwise.
     <p>
     A small note to plugin developers:
     This method is only intended to be called byt the AbstractLifecycle implementation.
     Please DO NOT check if the platform should perform a deployment while performing the transformation!
     */
    public boolean performDeployment() {
        try {
            String value = getInputs().get(Platform.DEPLOY_AFTER_TRANSFORMATION_KEY).orElse("false");
            return Boolean.parseBoolean(value);
        } catch (NoSuchPropertyException e) {
            return false;
        } catch (Exception e) {
            logger.error("Cannot parse deployment flag in properties", e);
            return false;
        }
    }
}
