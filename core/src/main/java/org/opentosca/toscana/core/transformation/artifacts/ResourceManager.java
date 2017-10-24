package org.opentosca.toscana.core.transformation.artifacts;

import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.ZipUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

@Service
public class ResourceManager
    implements ArtifactManagementService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Preferences preferences;
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy_hh-mm");

    @Autowired
    public ResourceManager(Preferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String saveToArtifactDirectory(File transformationWorkingDirectory, String csarName, String platformName)
        throws IOException {
        //TODO Determine better name for artifacts
        String filename = csarName + "-" + platformName + "_" + format.format(new Date(currentTimeMillis())) + ".zip";
        File outputFile = new File(preferences.getArtifactDir(), filename);

        FileOutputStream out = new FileOutputStream(outputFile);
        ZipUtility.compressDirectory(transformationWorkingDirectory, out);
        out.close();

        return "/artifacts/" + filename;
    }
}
