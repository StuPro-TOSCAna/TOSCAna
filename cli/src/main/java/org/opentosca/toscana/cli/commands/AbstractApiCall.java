package org.opentosca.toscana.cli.commands;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.CliProperties;
import org.opentosca.toscana.retrofit.util.LoggingMode;

import picocli.CommandLine.Option;

public abstract class AbstractApiCall implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable Info Level Process Output")
    private boolean showVerbose;

    @Option(names = {"-m", "--moreverbose"}, description = "Enable Debug Level Process Output")
    private boolean showMVerbose;

    private ApiController api;

    public AbstractApiCall() {
        CliProperties prop = new CliProperties();
        if (showMVerbose) {
            api = new ApiController(prop.getApiUrl(), LoggingMode.HIGH);
        } else if (showVerbose) {
            api = new ApiController(prop.getApiUrl(), LoggingMode.LOW);
        } else {
            api = new ApiController(prop.getApiUrl(), LoggingMode.OFF);
        }
    }

    public ApiController getApi() {
        return api;
    }
}
