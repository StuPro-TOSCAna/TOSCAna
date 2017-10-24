package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.Preferences;

public class FileSystemDummy extends FileSystem {

    public FileSystemDummy(Preferences preferences) {
        super(preferences);
    }

    @Override
    public long getUsedSpace() {
        return 100;
    }

    @Override
    public long getAvailableSpace() {
        return 1000;
    }
}
