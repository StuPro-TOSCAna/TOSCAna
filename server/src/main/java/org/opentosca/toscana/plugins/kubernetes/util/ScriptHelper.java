package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.apache.commons.io.IOUtils;

public class ScriptHelper {

    /**
     Resource path to the push script
     */
    private static final String PUSH_SCRIPT_RESOURCE_PATH = "/kubernetes/docker/push-images.sh";
    /**
     Resource path to the deploy script
     */
    private static final String DEPLOY_SCRIPT_RESOURCE_PATH = "/kubernetes/deploy.sh";

    /**
     The list of helper shell scripts
     */
    private static KubernetesScript[] SCRIPTS = {
        new KubernetesScript("deploy.sh", DEPLOY_SCRIPT_RESOURCE_PATH, true),
        new KubernetesScript("push-images.sh", PUSH_SCRIPT_RESOURCE_PATH, false)
    };

    /**
     This method copies the helper scripts of the Kubernetes Plugin into the output directory of the
     current

     @param push   should be set to true if the Images have already been pushed
     @param access the PluginFileAccess used for filesystem interaction
     @throws IOException Gets thrown if something goes wrong while writing the shell scripts
     */
    public static void copyScripts(boolean push, PluginFileAccess access) throws IOException {
        for (KubernetesScript script : SCRIPTS) {
            boolean copy = !push || script.copyIfPushed();
            if (copy) {
                InputStream in = ScriptHelper.class.getResourceAsStream(script.getResourcePath());
                access.access("/output/" + script.getFileName())
                    .append(IOUtils.toString(new InputStreamReader(in)))
                    .close();
                in.close();
            }
        }
    }
}
