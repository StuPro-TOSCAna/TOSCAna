package org.opentosca.toscana.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class CliProperties {

    public static final String CLI_PROPS_ENDPOINT_KEY = "endpoint";
    private static String filePath;
    private static String dataPath = null;
    private static final String dataPathWin = "/AppData/toscana";
    private static final String dataPathNix = "/.toscana";
    private static final String cliProp = "/cli.properties";
    private static Properties properties = new Properties();
    private static File file;
    private static String operatingSystem = null;

    /**
     * Creates a cli.properties config file
     */
    private static void createProperties() {
        try {
            FileWriter writer = new FileWriter(file);
            properties.setProperty(CLI_PROPS_ENDPOINT_KEY,"http://127.0.0.1:8080/");
            properties.store(writer, "Cli Settings");
            writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Gets the API Url from the cli.properties config, if the config doesn't exist a
     * default config gets created
     * @return API Url
     */
    public static String getApiUrl() {
        String url = "";
        setupPath();
        
        try {
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
        url += properties.getProperty(CLI_PROPS_ENDPOINT_KEY);
        return url;
    }

    /**
     * Sets the path for Unix or Windows Systems to the cli.properties file
     */
    public static void setupPath() {
        if (dataPath == null || dataPath.isEmpty()) {
            // init dataPath to platform dependent value
            dataPath = System.getProperty("user.home");
            if (operatingSystem == null) {
                operatingSystem = System.getProperty("os.name");
            }
            
            if (operatingSystem.contains("Linux") || operatingSystem.contains("Mac")) {
                dataPath += dataPathNix;
                filePath = dataPath + cliProp;
            } else if (operatingSystem.contains("Windows")) {
                dataPath += dataPathWin;
                filePath = dataPath + cliProp;
            } else {
                dataPath = FileUtils.getTempDirectory() + File.separator + "toscana";
                filePath = dataPath + cliProp;
                System.err.println("fallback value for datadir not defined for this platform. Falling back to tmp dir");
            }
        }
        File dataDir = new File(dataPath);
        file = new File(filePath);

        if (!dataDir.exists()) {
            dataDir.mkdirs();
            if (!dataDir.exists()) {
                System.err.println("Failed to create data directory");
            }
        }

        if (!new File(filePath).exists()) {
            createProperties();
        }
    }    
}