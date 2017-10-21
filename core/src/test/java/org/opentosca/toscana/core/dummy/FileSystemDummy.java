package org.opentosca.toscana.core.dummy;

import org.opentosca.toscana.core.util.FileSystem;

public class FileSystemDummy extends FileSystem {

    @Override
    public long getUsedSpace() {
        return 100;
    }

    @Override
    public long getAvailableSpace() {
        return 1000;
    }
}
