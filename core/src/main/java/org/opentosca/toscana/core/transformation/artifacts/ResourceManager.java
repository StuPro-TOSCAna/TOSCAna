package org.opentosca.toscana.core.transformation.artifacts;

import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ResourceManager
    implements ArtifactManagementService {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Preferences preferences;
    @Autowired
    public ResourceManager(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String saveToArtifactDirectory(File transformationWorkingDirectory) {

        return null;
    }
}
