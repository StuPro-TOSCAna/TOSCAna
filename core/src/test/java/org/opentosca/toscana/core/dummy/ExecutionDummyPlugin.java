package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.plugin.AbstractPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;

public class ExecutionDummyPlugin extends AbstractPlugin {

    protected final boolean failDuringExec;

    public ExecutionDummyPlugin(Platform platform, boolean failDuringExec) {
        super(platform);
        this.failDuringExec = failDuringExec;
    }

    @Override
    public void transform(TransformationContext transformation) throws Exception {
        Logger logger = transformation.getLogger(getClass());
        int i = 0;
        transformation.getPluginFileAccess().access("some-output-file").append("some transformation result").close();
        logger.info("Waiting 50ms until completion");
        while (!Thread.currentThread().isInterrupted() && i < 5) {
            Thread.sleep(10);
            i++;
        }
        if (failDuringExec) {
            logger.info("Throwing test exception");
            throw new InterruptedException("Test Exception");
        }
    }
}
