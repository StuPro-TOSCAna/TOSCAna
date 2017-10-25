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
public class ArtifactManagementServiceImpl
    implements ArtifactManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Preferences preferences;
    private final TransformationDao transformatioDao;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy_hh-mm");

    @Autowired
    public ArtifactManagementServiceImpl(Preferences preferences, TransformationDao transformationDao) {
        this.preferences = preferences;
        this.transformatioDao = transformationDao;
    }

    @Override
    public String serveArtifact(Transformation transformation) throws IOException {
        String csarName = transformation.getCsar().getIdentifier();
        String platformName = transformation.getPlatform().id;
        File transformationWorkingDirectory = transformatioDao.getRootDir(transformation);
        //TODO Determine better name for artifacts
        String filename = csarName + "-" + platformName + "_" + format.format(new Date(currentTimeMillis())) + ".zip";
        File outputFile = new File(preferences.getArtifactDir(), filename);

        FileOutputStream out = new FileOutputStream(outputFile);
        log.info("Writing artifact data to {}", outputFile.getAbsolutePath());
        ZipUtility.compressDirectory(transformationWorkingDirectory, out);
        out.close();

        return "/artifacts/" + filename;
    }
}
