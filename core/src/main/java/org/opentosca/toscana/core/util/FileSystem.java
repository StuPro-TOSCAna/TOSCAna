package org.opentosca.toscana.core.util;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class FileSystem {

    private Preferences preferences;

    /**
     * Returns the (by the CSARS and targetArtifacts) used storage space in MB.
     */
    public long getUsedSpace() {
        File dir = preferences.getDataDir();
        return (dir.getTotalSpace() - dir.getFreeSpace()) / (long) Math.pow(1024, 2);
    }

    /**
     * Returns the available storage space in MB.
     */
    public long getAvailableSpace() {
        File dataDir = preferences.getDataDir();
        long freeBytes = dataDir.getFreeSpace();
        long freeMegabytes = freeBytes / (long) Math.pow(1024, 2);
        return freeMegabytes;
    }

    @Autowired
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
