package org.opentosca.toscana.plugins.cloudformation.util;

import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.plugins.scripts.BashScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.plugins.cloudformation.CloudFormationModule.FILEPATH_TARGET;

public class CloudFormationScript extends BashScript {

    private final static Logger logger = LoggerFactory.getLogger(BashScript.class);

    /**
     Creates a bash scriptPath in the <i>output/files/</i> directory in the transformation content directory and
     marks it as a util file to be uploaded
     <p>
     These are scripts that need to be uploaded
     */
    public CloudFormationScript(PluginFileAccess access, String name) throws IOException {
        super(access, name);
    }

    protected void setUpScript() throws IOException {
        scriptPath = FILEPATH_TARGET + name + ".sh";

        logger.info("Creating new bash scriptPath: " + this.scriptPath);
        access.delete(scriptPath);

        access.access(scriptPath).appendln(SHEBANG).close();
    }
}
