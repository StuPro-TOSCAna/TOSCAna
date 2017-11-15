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
    private final String CLI_DEFAULT_ENDPOINT = "http://127.0.0.1:8084/";
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
            properties.setProperty(CLI_PROPS_ENDPOINT_KEY, CLI_DEFAULT_ENDPOINT);
            properties.store(writer, "Cli Settings");
            writer.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while trying to store the cli.properties config file");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     Gets the API Url from the cli.properties config, if the config doesn't exist a default config gets created

     @return API Url
     */
    public String getApiUrl() {
        String url = "";

        try {
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while trying to load the API Endpoint.");
            e.printStackTrace();
            System.exit(1);
        }

        url += properties.getProperty(CLI_PROPS_ENDPOINT_KEY);
        boolean containsQuotationMark = url.contains("\"");
        if (containsQuotationMark) {
            url = url.replaceAll("\"", "");
            System.out.println("Given URL has a illegal quotation marks.");
            System.out.println("Removing the illegal characters.");
        }
        if (url.length() > 0) {
            return url;
        } else {
            createProperties();
            System.err.println(String.format("API Endpoint not correctly set, using default value '%s'", CLI_DEFAULT_ENDPOINT));
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

            final String cliProp = "cli.properties";
            if (operatingSystem.contains("Linux") || operatingSystem.contains("Mac")) {
                dataDir = new File(dataPath, ".toscana");
                file = new File(dataDir, cliProp);
            } else if (operatingSystem.contains("Windows")) {
                dataDir = new File(dataPath, "AppData/toscana");
                file = new File(dataDir, cliProp);
            } else {
                dataPath = FileUtils.getTempDirectory() + File.separator + "toscana";
                dataDir = new File(dataPath);
                file = new File(dataDir, cliProp);
                System.err.println(String.format("fallback value for data directory not defined for current platform '%s'. Falling back to '%s'", operatingSystem, dataDir.getPath()));
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
