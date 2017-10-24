package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

/**
 * This class describes a Abstract plugin and implements some things that you would have to implement
 * manually when using the plugin interface
 */
public abstract class AbstractPlugin implements TransformationPlugin {

    /**
     * This logging object can be used for non transformation specific logging within the subclasses
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public AbstractPlugin() {
        this.init();
        logger.info("Initialized '{}' Plugin.", getName());
    }

    /**
     * @return the name of the plugins (All chars allowed)
     */
    public abstract String getName();

    /**
     * @return the identifier for the plugin.
     */
    public abstract String getIdentifier();

    /**
     * @return The plugin specific properties like enpoints get provided by this mehtod
     */
    public HashSet<Property> getPluginSpecificProperties() {
        return new HashSet<>();
    }

    /**
     * This method is called during the initialisation of the plugin (from within the constructor)
     * <p>
     * It is meant to initialize plugin specific things (Maybe this is not even needed)
     */
    protected void init() {
        //Empty method, allows plugin to run code on initialisation
    }

    @Override
    public Platform getPlatformDetails() {
        return new Platform(getIdentifier(), getName(), getPluginSpecificProperties());
    }
}
