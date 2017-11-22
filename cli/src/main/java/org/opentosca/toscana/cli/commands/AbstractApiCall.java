package org.opentosca.toscana.cli.commands;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Option;

public abstract class AbstractApiCall implements Runnable {

    @Option(names = {"-v", "--verbose"}, description = "Enable Info Level Process Output")
    private boolean showVerbose;

    @Option(names = {"-m", "--moreverbose"}, description = "Enable Debug Level Process Output")
    private boolean showMVerbose;

    private ApiController api;

    public AbstractApiCall() {
        if (showMVerbose) {
            api = new ApiController(ApiController.Mode.HIGH);
        } else if (showVerbose) {
            api = new ApiController(ApiController.Mode.LOW);
        } else {
            api = new ApiController(ApiController.Mode.NONE);
        }
    }

    public ApiController getApi() {
        return api;
    }
}
