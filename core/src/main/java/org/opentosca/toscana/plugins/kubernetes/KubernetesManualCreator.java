package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

public class KubernetesManualCreator {
    /**
     * This method fills the app name and the kubernetes resource file name into the generic template
     */
    public static String createManual(String appName, String resFileName) throws IOException {
        File manual = new File("src/main/resources/kubernetes/k8s_manual_guide.md");
        String result = FileUtils.readFileToString(manual, Charsets.UTF_8);
        result = result.replaceAll("\\{app_name}", appName);
        result = result.replaceAll("\\{k8s_resource_file}", resFileName);
        return result;
    }
}
