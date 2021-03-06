package org.opentosca.toscana.cli.commands;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.retrofit.util.LoggingMode;

import picocli.CommandLine;

public abstract class AbstractApiCall implements Runnable {

    @CommandLine.Option(names = {"-m", "--moreverbose"}, description = "Enable Debug Level Process Output")
    public boolean showMVerbose;
    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Enable Info Level Process Output")
    public boolean showVerbose;
    protected ApiController api;

    public AbstractApiCall() {

    }

    public void createApi(ApiController apiController) {
        api = apiController;
        api.setLoggingMode(getLoggingMode(showVerbose, showMVerbose));
    }

    /**
     Get's and returns the LoggingMode which is wanted for the CLI to output more Information

     @param verbose     need for an output that is more then default
     @param moreVerbose need for a detailed output of what is processed in the CLI
     @return the Loggingmode for the API
     */
    public LoggingMode getLoggingMode(boolean verbose, boolean moreVerbose) {
        if (verbose) {
            return LoggingMode.LOW;
        } else if (moreVerbose) {
            return LoggingMode.HIGH;
        } else {
            return LoggingMode.OFF;
        }
    }
}
