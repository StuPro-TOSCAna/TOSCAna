package org.opentosca.toscana.plugins.kubernetes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ManualCreator {
    /**
     This method fills the app name and the kubernetes resource file name into the generic template
     */
    public static String createManual(String appName, String resFileName) throws IOException {
        Class clazz = ManualCreator.class;
        InputStream manual = clazz.getResourceAsStream("/kubernetes/kubernetes_manual_guide.md");
        String result = IOUtils.toString(manual);
        manual.close();
        result = result.replaceAll("\\{app_name}", appName);
        result = result.replaceAll("\\{kubernetes_resource_file}", resFileName);
        return result;
    }
}
