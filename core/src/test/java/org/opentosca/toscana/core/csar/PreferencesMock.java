package org.opentosca.toscana.core.csar;

import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PreferencesMock implements Preferences {

    private final static Logger logger = LoggerFactory.getLogger(PreferencesMock.class.getName());

    private File dataDir;

    @Override
    public String get(Key key) {
        return "";
    }

    /**
     * Returns a temp file. Gets deleted on VM exit
     */
    @Override
    public File getDataDir() {
        if (dataDir == null){
            try {
                dataDir = File.createTempFile("toscana_test_", "");
                dataDir.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //dataDir.deleteOnExit();
        }
        return dataDir;
    }
}
