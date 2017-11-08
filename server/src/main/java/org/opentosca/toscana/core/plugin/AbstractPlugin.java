package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class describes a Abstract plugin and implements some things that you would have to implement manually when
 using the plugin interface
 */
public abstract class AbstractPlugin implements TransformationPlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Platform platform;

    public AbstractPlugin(Platform platform) {
        this.platform = platform;
        if (platform == null) {
            throw new IllegalStateException("Platform must not be null");
        }
        this.init();
        logger.info("Initialized '{}' plugin.", platform.name);
    }

    /**
     Is called during the initialisation of the plugin
     */
    protected void init() {
        //Empty method, allows plugin to run code on initialisation
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }
}
