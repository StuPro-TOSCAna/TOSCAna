package org.opentosca.toscana.core.testdata.dummyplugins;

import org.opentosca.toscana.core.plugin.lifecycle.AbstractLifecycle;
import org.opentosca.toscana.core.plugin.lifecycle.ToscanaPlugin;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.slf4j.Logger;

public class ExecutionDummyPlugin extends ToscanaPlugin {

    protected final boolean failDuringExec;

    public ExecutionDummyPlugin(Platform platform, boolean failDuringExec) {
        super(platform);
        this.failDuringExec = failDuringExec;
    }

    @Override
    public void transform(AbstractLifecycle lifecycle) throws Exception {
        TransformationContext context = lifecycle.getContext();
        Logger logger = context.getLogger(getClass());
        int i = 0;
        context.getPluginFileAccess().access("some-output-file").append("some transformation result").close();
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
    public AbstractLifecycle getInstance(TransformationContext context) throws Exception {
        return new AbstractLifecycle(context) {
            @Override
            public boolean checkModel() {
                return true;
            }

            @Override
            public void prepare() {
                // noop
            }

            @Override
            public void transform() {
                // noop
            }

            @Override
            public void cleanup() {
                // noop
            }
        };
    }
}
