package org.opentosca.toscana.core.transformation.artifacts;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.util.ZipUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtifactServiceImpl implements ArtifactService {

    private final TransformationDao transformationDao;

    @Autowired
    public ArtifactServiceImpl(TransformationDao transformationDao) {
        this.transformationDao = transformationDao;
    }

    @Override
    public TargetArtifact serveArtifact(Transformation transformation) throws IOException {
        File transformationContentDir = transformationDao.getContentDir(transformation);
        TargetArtifact artifact = transformationDao.createTargetArtifact(transformation);
        transformation.setTargetArtifact(artifact);
        OutputStream stream = artifact.writeAccess();
        ZipUtility.compressDirectory(transformationContentDir, stream);
        stream.close();
        return artifact;
    }
}
