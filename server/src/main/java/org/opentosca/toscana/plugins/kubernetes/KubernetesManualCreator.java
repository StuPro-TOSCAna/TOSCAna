package org.opentosca.toscana.plugins.kubernetes;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

public class KubernetesManualCreator {
    /**
     This method fills the app name and the kubernetes resource file name into the generic template
     */
    public static String createManual(String appName, String resFileName) throws IOException {
        Class clazz = KubernetesManualCreator.class;
        File manual = new File(clazz.getResource("/kubernetes/kubernetes_manual_guide.md").getPath());
        String result = FileUtils.readFileToString(manual, Charsets.UTF_8);
        result = result.replaceAll("\\{app_name}", appName);
        result = result.replaceAll("\\{kubernetes_resource_file}", resFileName);
        return result;
    }
}
