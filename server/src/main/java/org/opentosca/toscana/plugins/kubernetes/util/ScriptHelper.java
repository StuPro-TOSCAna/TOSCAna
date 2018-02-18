package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.opentosca.toscana.core.plugin.PluginFileAccess;

import org.apache.commons.io.IOUtils;

public class ScriptHelper {

    private static final String PUSH_SCRIPT_RESOURCE_PATH = "/kubernetes/docker/push-images.sh";
    private static final String DEPLOY_SCRIPT_RESOURCE_PATH = "/kubernetes/deploy.sh";

    private static KubernetesScript[] SCRIPTS = {
        new KubernetesScript("deploy.sh", DEPLOY_SCRIPT_RESOURCE_PATH, true),
        new KubernetesScript("push-images.sh", PUSH_SCRIPT_RESOURCE_PATH, false)
    };

    public static void copyScripts(boolean push, PluginFileAccess access) throws IOException {
        for (KubernetesScript script : SCRIPTS) {
            boolean copy = !push || script.copyIfPushed();
            if (copy) {
                InputStream in = ScriptHelper.class.getResourceAsStream(script.getResourcePath());
                access.access("/output/" + script.getName())
                    .append(IOUtils.toString(new InputStreamReader(in)))
                    .close();
                in.close();
            }
        }
    }
}
