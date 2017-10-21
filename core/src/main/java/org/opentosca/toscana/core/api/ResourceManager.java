package org.opentosca.toscana.core.api;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.artifacts.ArtifactManagementService;
import org.springframework.stereotype.Service;

@Service
public class ResourceManager
    implements ArtifactManagementService {

    @Override
    public String saveToArtifactDirectory(Transformation transformation) {

        return null;
    }
}
