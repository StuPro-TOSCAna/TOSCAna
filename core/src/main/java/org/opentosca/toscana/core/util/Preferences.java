package org.opentosca.toscana.core.util;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 Manages all preferences. Uses Spring Properties (therefore looks in property files, java flags and system environment
 for values)
 */
@Service
public class Preferences {

    private static final Logger logger = LoggerFactory.getLogger(Preferences.class.getName());

    @Value("${datadir}")
    private String dataPath;
    @Value("${datadir_win}")
    private String dataPathFallbackWin;
    @Value("${datadir_nix}")
    private String dataPathFallbackNix;

    private File dataDir;

    @PostConstruct
    public void setup() {
        if (dataPath == null || dataPath.isEmpty()) {
            // init dataPath to platform dependent value
            if (OsUtils.isUnix() || OsUtils.isMac()) {
                dataPath = dataPathFallbackNix;
            } else if (OsUtils.isWindows()) {
                dataPath = dataPathFallbackWin;
            } else {
                logger.warn("fallback value for datadir not defined for this platform. Falling back to tmp dir");
                dataPath = FileUtils.getTempDirectory() + File.separator + "toscana";
            }
        }
        dataDir = new File(dataPath);
        dataDir.mkdirs();
        logger.info("Data directory is {}", dataDir.getAbsolutePath());
        if (!dataDir.exists()) {
            logger.error("Failed to create data directory");
        }
    }

    public File getDataDir() {
        return dataDir;
    }
}
