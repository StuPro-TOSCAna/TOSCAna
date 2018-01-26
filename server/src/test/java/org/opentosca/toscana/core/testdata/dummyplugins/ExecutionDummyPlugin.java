package org.opentosca.toscana.core.testdata.dummyplugins;

import org.opentosca.toscana.core.plugin.TOSCAnaPlugin;
import org.opentosca.toscana.core.plugin.lifecycle.TransformationLifecycle;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;

public class ExecutionDummyPlugin extends TOSCAnaPlugin {

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
        logger.info("Waiting 500ms until completion");
        while (!Thread.currentThread().isInterrupted() && i < 5) {
            Thread.sleep(100);
            i++;
        }
        if (failDuringExec) {
            logger.info("Throwing test exception");
            throw new InterruptedException("Test Exception");
        }
    }

    @Override
    protected TransformationLifecycle getInstance(TransformationContext context) throws Exception {
        return null;
    }
}
