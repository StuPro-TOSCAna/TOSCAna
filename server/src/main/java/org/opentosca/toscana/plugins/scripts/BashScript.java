package org.opentosca.toscana.plugins.scripts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BashScript {
    private final static Logger logger = LoggerFactory.getLogger(BashScript.class);
    private String name;
    private PluginFileAccess access;
    private final String scriptsTargetDir = "content/scripts/";
    private File script;

    public BashScript(PluginFileAccess access, String name) throws IOException {
        this.access = access;
        this.name = name;

        this.copyUtilScriptsIfNotExistent();
        this.setUpScript();
    }

    private void setUpScript() throws IOException {
        File scriptsFolder = new File(access.getAbsolutePath(scriptsTargetDir));
        script = new File(scriptsFolder, name + ".sh");

        if (script.createNewFile()) {
            logger.info("Creating new bash script: " + script);
        } else {
            logger.info("Overwriting bash script: " + script);
            script.delete();
            script.createNewFile();
        }

        FileWriter writer = new FileWriter(script);
        writer.append("#!/bin/sh\n");
        writer.append("source util/*\n");
        writer.close();
    }

    private void copyUtilScriptsIfNotExistent() throws IOException {
        File targetArtifactScript = new File(access.getAbsolutePath(""));
        File targetScriptUtils = new File(targetArtifactScript, scriptsTargetDir);
        if (targetScriptUtils.exists()) return;
        targetScriptUtils.mkdirs();
        File sourceScriptUtils = new File(getClass().getResource("/plugins/scripts/util/").getFile());
        FileUtils.copyDirectory(sourceScriptUtils, targetScriptUtils);
    }

    public void append(String string) throws IOException {
        FileWriter fileWriter = new FileWriter(script);
        fileWriter.append(string + "\n");
        fileWriter.close();
    }
}
