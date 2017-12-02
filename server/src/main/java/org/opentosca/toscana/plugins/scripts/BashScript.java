package org.opentosca.toscana.plugins.scripts;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.SCRIPTS_DIR_PATH;
import static org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle.UTIL_DIR_NAME;

public class BashScript {
    public static final String SHEBANG = "#!/bin/sh";
    private final static Logger logger = LoggerFactory.getLogger(BashScript.class);
    private String name;
    private PluginFileAccess access;
    private String scriptPath;

    /**
     Creates a bash scriptPath in the <i>content/scripts/</i> directory in the transformation content directory.

     @param access PluginFileAccess
     @param name   Script name without the extension
     */
    public BashScript(PluginFileAccess access, String name) throws IOException {
        this.access = access;
        this.name = name;

        this.setUpScript();
    }

    private void setUpScript() throws IOException {
        scriptPath = SCRIPTS_DIR_PATH + name + ".sh";

        logger.info("Creating new bash scriptPath: " + this.scriptPath);
        access.delete(scriptPath);

        access.access(scriptPath).append(SHEBANG + "\n")
            .append("source " + UTIL_DIR_NAME + "*\n")
            .close();
    }

    public void append(String string) throws IOException {
        logger.debug("Appending {} to {}.sh", string, name);
        access.access(scriptPath).append(string + "\n").close();
    }
    
}
