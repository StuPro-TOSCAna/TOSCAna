package org.opentosca.toscana.core.util;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface Preferences {

    public enum Key {
        /**
         * the root directory all data is persisted to
         */
        TOSCANA_DATADIR
    }


    /**
     * Returns the content of the system environment variable matching given key
     */
    public String get(Preferences.Key key);

    /**
     * Returns the root data dir of the whole application
     * This dir is the place where all artifacts are stored
     */
    public File getDataDir();

}
