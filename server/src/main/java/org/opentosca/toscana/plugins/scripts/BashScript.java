package org.opentosca.toscana.plugins.scripts;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScript {
    private final static Logger logger = LoggerFactory.getLogger(BashScript.class);
    private final String scriptsTargetDir = "content/scripts/";
    private String name;
    private PluginFileAccess access;
    private File script;

    public BashScript(PluginFileAccess access, String name) throws IOException {
        this.access = access;
        this.name = name;

        this.copyUtilScriptsIfNotExistent();
        this.setUpScript();
    }

    private void setUpScript() throws IOException {
        File scriptsFolder = new File(scriptsTargetDir);
        script = new File(scriptsFolder, "test.sh");

        if (access.fileExists(script.getPath())) {
            logger.info("Creating new bash script: " + script);
        } else {
            logger.info("Overwriting bash script: " + script);
            access.delete(script.getPath());
        }

        access.access(script.getPath()).append("#!/bin/sh\n")
            .append("source util/*\n")
            .close();
    }

    private void copyUtilScriptsIfNotExistent() throws IOException {
        String utilFolderPath = scriptsTargetDir + "/util/";
        File targetScriptUtilsFolder = new File(utilFolderPath);
        if (access.fileExists(targetScriptUtilsFolder.getPath())) return;
        logger.info("Copying util scripts to target artifact.");
        access.createFolder(utilFolderPath);
        File sourceScriptUtils = new File(getClass().getResource("/plugins/scripts/util/").getFile());
        FileUtils.copyDirectory(sourceScriptUtils, new File(access.getAbsolutePath(targetScriptUtilsFolder.getPath())));
    }

    public void append(String string) throws IOException {
        logger.info("Appending {} to {}.sh", string, name);
        access.access(script.toString()).append(string + "\n").close();
    }
}
