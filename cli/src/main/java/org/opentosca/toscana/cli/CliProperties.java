package org.opentosca.toscana.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class CliProperties {

    public final String CLI_PROPS_ENDPOINT_KEY = "endpoint";
    private final Properties properties = new Properties();
    private String filePath;
    private String dataPath = null;
    private File file;
    private String operatingSystem = null;

    public CliProperties() {
        setupPath();
    }

    /**
     Creates a cli.properties config file
     */
    private void createProperties() {
        try {
            FileWriter writer = new FileWriter(file);
            properties.setProperty(CLI_PROPS_ENDPOINT_KEY, "http://127.0.0.1:8084/");
            properties.store(writer, "Cli Settings");
            writer.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while trying to store the cli.properties config file");
            e.printStackTrace();
        }
    }

    /**
     Gets the API Url from the cli.properties config, if the config doesn't exist a default config gets created

     @return API Url
     */
    public String getApiUrl() {
        String url;

        try {
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while trying to load the API Endpoint.");
            e.printStackTrace();
        }

        url = properties.getProperty(CLI_PROPS_ENDPOINT_KEY);

        if (url.length() > 0) {
            return url;
        } else {
            createProperties();
            try {
                throw new IOException("API Endpoint not correctly set, using default value");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            return getApiUrl();
        }
    }

    /**
     Sets the path for Unix or Windows Systems to the cli.properties file
     */
    private void setupPath() {
        File dataDir = null;
        if (dataPath == null || dataPath.isEmpty()) {
            // init dataPath to platform dependent value
            dataPath = System.getProperty("user.home");
            if (operatingSystem == null) {
                operatingSystem = System.getProperty("os.name");
            }

            final String cliProp = "/cli.properties";
            if (operatingSystem.contains("Linux") || operatingSystem.contains("Mac")) {
                dataDir = new File(dataPath, "/.toscana");
                file = new File(dataDir.getPath(), cliProp);
            } else if (operatingSystem.contains("Windows")) {
                dataDir = new File(dataPath, "/AppData/toscana");
                file = new File(dataDir.getPath(), cliProp);
            } else {
                dataPath = FileUtils.getTempDirectory() + File.separator + "toscana";
                dataDir = new File(dataPath);
                file = new File(dataDir.getPath(), cliProp);
                System.err.println(String.format("fallback value for datadir not defined for current platform '%s'. Falling back to '%s'", operatingSystem, filePath));
            }
        }

        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
            if (!dataDir.exists()) {
                System.err.println("Failed to create data directory");
            }
        }

        if (!file.exists()) {
            createProperties();
        }
    }
}
