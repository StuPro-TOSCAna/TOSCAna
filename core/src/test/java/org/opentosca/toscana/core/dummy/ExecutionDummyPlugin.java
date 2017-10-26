package org.opentosca.toscana.core.dummy;

import java.util.HashSet;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.slf4j.Logger;

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
    public void transform(TransformationContext transformation) throws Exception {
        Logger logger = transformation.getLogger(getClass());
        int i = 0;
        transformation.getPluginFileAccess().write("some-output-file").append("some transformation result").close();
        logger.info("Waiting 50ms until completion");
        while (!Thread.currentThread().isInterrupted() && i < 5) {
            Thread.sleep(10);
            i++;
        }
        if (failDuringExec) {
            logger.info("Throwing test exception");
            throw new RuntimeException("Test Exception");
        }
    }
}
