package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.util.Preferences;

import java.io.File;
import java.io.IOException;

public class TestPreferencesImpl implements Preferences {

    private File dataDir;

    @Override
    public String get(Key key) {
        return "";
    }

    @Override
    public File getDataDir() {
        if (dataDir == null){
            try {
                dataDir = File.createTempFile("toscana_test_", "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataDir.deleteOnExit();
        }
        return dataDir;
    }
}
