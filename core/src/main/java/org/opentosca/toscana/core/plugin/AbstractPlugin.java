package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

public abstract class AbstractPlugin implements TransformationPlugin {

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

    public abstract HashSet<Property> getPluginSpecificProperties();

    public void init() {
        //Empty method, allows plugin to run code on initialisation
    }
    
    @Override
    public Platform getPlatformDetails() {
        return new Platform(getIdentifier(), getName(), getPluginSpecificProperties());
    }

    @Override
    public void transform(Transformation transformation) throws Exception {

    }
}
