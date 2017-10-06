package org.opentosca.toscana.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class Preferences {
	
	private String dataPath;

    private static final Logger logger = LoggerFactory.getLogger(Preferences.class.getName());
    
    private enum Key {
    	TOSCANA_DATADIR
	}


    private final Map<Key, String> defaults;

    public Preferences() {
        defaults = getDefaults();
        logger.error("datapath is '{}'");
    }

    private Map<Key, String> getDefaults() {
        Map<Key, String> defaultMap = new HashMap<>();
        String datadir = System.getenv("user.home");
        if (OsUtils.isUnix() || OsUtils.isMac()) {
            datadir = System.getenv("user.home") + "/.toscana";
        } else if (OsUtils.isWindows()) {
            datadir = System.getenv("user.home") + "/AppData/toscana";
        }
        defaultMap.put(Key.TOSCANA_DATADIR, datadir);
        return defaultMap;
    }

    public String get(Key key) {
        String value = System.getenv(key.name());
        if (value == null || value.isEmpty()) {
            logger.info("no value supplied for option '{}'", key);
            value = defaults.get(key);
            if (value == null){
                logger.warn("fallback value for option '{}' not defined", key);
            }
        }
        return value;
    }

    public File getDataDir() {
        String dataDirPath = get(Key.TOSCANA_DATADIR);
        File dataDir = new File(dataDirPath);
        dataDir.mkdirs();
        return dataDir;
    }
}
