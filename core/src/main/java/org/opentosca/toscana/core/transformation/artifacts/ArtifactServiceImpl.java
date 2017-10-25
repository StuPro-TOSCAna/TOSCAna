package org.opentosca.toscana.core.transformation.artifacts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.ZipUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.lang.System.currentTimeMillis;

@Service
public class ArtifactServiceImpl
    implements ArtifactService {

    private final static String ARTIFACT_DIR = "artifacts";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TransformationDao transformatioDao;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy_hh-mm");
    private final File artifactDir;

    @Autowired
    public ArtifactServiceImpl(Preferences preferences, TransformationDao transformationDao) {
        this.transformatioDao = transformationDao;
        artifactDir = new File(preferences.getDataDir(), ARTIFACT_DIR);
        artifactDir.mkdir();
        logger.info("Artifact directory is {}", artifactDir.getAbsolutePath());
    }

    @Override
    public TargetArtifact serveArtifact(Transformation transformation) throws IOException {
        String csarId = transformation.getCsar().getIdentifier();
        String platformId = transformation.getPlatform().id;
        File transformationWorkingDirectory = transformatioDao.getRootDir(transformation);
        String filename = csarId + "-" + platformId + "_" + format.format(new Date(currentTimeMillis())) + ".zip";
        File outputFile = new File(artifactDir, filename);

        FileOutputStream out = new FileOutputStream(outputFile);
        logger.info("Writing artifact data to {}", outputFile.getAbsolutePath());
        ZipUtility.compressDirectory(transformationWorkingDirectory, out);
        out.close();

        return new TargetArtifact("/artifacts/" + filename);
    }

    @Override
    public File getArtifactDir() {
        return artifactDir;
    }
}
