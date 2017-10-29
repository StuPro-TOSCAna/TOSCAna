package org.opentosca.toscana.core.util;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileSystem {

    private Preferences preferences;

    @Autowired
    public FileSystem(Preferences preferences) {
        this.preferences = preferences;
    }

    /**
     Returns the (by the csars and targetArtifacts) used storage space in MB.
     */
    public long getUsedSpace() {
        File dir = preferences.getDataDir();
        return (dir.getTotalSpace() - dir.getFreeSpace()) / (long) Math.pow(1024, 2);
    }

    /**
     Returns the available storage space in MB.
     */
    public long getAvailableSpace() {
        File dataDir = preferences.getDataDir();
        long freeBytes = dataDir.getFreeSpace();
        return freeBytes / (long) Math.pow(1024, 2);
    }
}
