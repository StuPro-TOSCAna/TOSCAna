package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

/**
 This interface has to be implemented in order to describe a TOSCAna Plugin
 */
public interface TransformationPlugin {

    /**
     @return The supported platform of the plugin, containing the display name, identifier and the PlatformProperties.
     Must not be null
     */
    Platform getPlatform();

    /**
     This method will transform  given model (contained in the TransformationContext instance) and will store the result
     in a directory provided by the context.

     @param context context for the transformation
     @throws Exception if any exception gets thrown the Transformation service will cancel the transformation
     */
    void transform(TransformationContext context) throws Exception;
}
