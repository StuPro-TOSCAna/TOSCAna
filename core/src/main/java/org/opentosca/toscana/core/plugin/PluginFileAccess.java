package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;

import java.io.File;

public class PluginFileAccess {
    private File csarRootPath;
    private File transformationRootPath;

    //TODO Implement mechanism to get the paths
    public PluginFileAccess(Csar csar, Transformation transformation) {
        this.csarRootPath = csarRootPath;
        this.transformationRootPath = transformationRootPath;
    }
    
    public boolean copy(String relativePath) {
        //TODO Implement Copy mechanism
        return false;
    }
    
    public File createFile(String relativePath) {
        //TODO Implement creation mechanism
        return null;
    }
}
