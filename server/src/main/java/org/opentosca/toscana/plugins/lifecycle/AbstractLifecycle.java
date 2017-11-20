package org.opentosca.toscana.plugins.lifecycle;

import java.io.File;
import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.TransformationContext;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

/**
 This class describes a base class that can be used by the plugins to implement
 their lifecycle. This already contains the context and a transformation specific logger
 */
public abstract class AbstractLifecycle implements TransformationLifecycle {

    /**
     The transformation specific logger that can be used to log to the transformations Log
     Object
     */
    protected Logger logger;
    /**
     The context on which the transformation should be transformed
     */
    protected TransformationContext context;

    /**
     @param context because the context is always needed this should never be null
     It probably gets called by the <code>getInstance</code> method of the LifecycleAwarePlugin
     */
    public AbstractLifecycle(@NotNull TransformationContext context) throws IOException {
        this.context = context;
        this.logger = context.getLogger(getClass());
        PluginFileAccess access = context.getPluginFileAccess();

        setUpFolders(access);
    }

    private void setUpFolders(PluginFileAccess access) throws IOException {
        String folder = "output/scripts/util";
        access.createFolder(folder);

        File sourceScriptUtils = new File(getClass().getResource("/plugins/scripts/util/").getFile());
        FileUtils.copyDirectory(sourceScriptUtils, new File(access.getAbsolutePath(folder)));
    }
}
