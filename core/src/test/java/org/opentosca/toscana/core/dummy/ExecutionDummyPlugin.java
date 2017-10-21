package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.slf4j.Logger;

import java.util.HashSet;

public class ExecutionDummyPlugin extends AbstractPlugin {
    
    private String name;
    private boolean failDuringExec;

    public ExecutionDummyPlugin(String name, boolean failDuringExec) {
        this.name = name;
        this.failDuringExec = failDuringExec;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public HashSet<Property> getPluginSpecificProperties() {
        return new HashSet<>();
    }

    @Override
    public void transform(Transformation transformation) throws Exception {
        Logger logger = transformation.getTransformationLogger(getClass());
        int i = 0;
        logger.info("Waiting 1s until completion");
        while(!Thread.currentThread().isInterrupted() && i < 100) {
            Thread.sleep(10);
            i++;
        }
        if(failDuringExec) {
            logger.info("Throwing test exception");
            throw new RuntimeException("Test Exception");
        }
    }
}
