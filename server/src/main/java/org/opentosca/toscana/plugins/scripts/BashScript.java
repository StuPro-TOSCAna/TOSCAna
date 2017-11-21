package org.opentosca.toscana.plugins.scripts;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScript {
    public static final String SHEBANG = "#!/bin/sh";
    private final static Logger logger = LoggerFactory.getLogger(BashScript.class);
    private final String scriptsTargetDir = "content/scripts/";
    private String name;
    private PluginFileAccess access;
    private String scriptPath;

    /**
     Creates a bash scriptPath in the <i>content/scripts</i> folder in the transformation content directory.
     If not already present, it also copies the util scripts into the <i>content/scripts/util</i> folder.

     @param access PluginFileAccess
     @param name   Script name without the extension
     */
    public BashScript(PluginFileAccess access, String name) throws IOException {
        this.access = access;
        this.name = name;

        this.setUpScript();
    }

    private void setUpScript() throws IOException {
        scriptPath = scriptsTargetDir + name + ".sh";

        logger.info("Creating new bash scriptPath: " + this.scriptPath);
        access.delete(scriptPath);

        access.access(scriptPath).append(SHEBANG + "\n")
            .append("source util/*\n")
            .close();
    }

    public void append(String string) throws IOException {
        logger.debug("Appending {} to {}.sh", string, name);
        access.access(scriptPath.toString()).append(string + "\n").close();
    }
}
