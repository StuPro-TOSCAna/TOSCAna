package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

/**
 * This interface has to be implemented in order to describe a TOSCAna Plugin
 */
public interface TransformationPlugin {
    /**
     * @return The platform details for the plugin, containing the display name,
     * identifier and the PlatformProperties.
     * <p>
     * This is required to not be null!
     */
    Platform getPlatformDetails();

    /**
     * This method will transform a given Modell (in the context) and will store the result in a directory provided by the context.
     *
     * @param context The transformation to perform the transformation with
     * @throws Exception if any exception gets thrown the Transformation service will cancel the transformation
     */
    void transform(TransformationContext context) throws Exception;
}
