package org.opentosca.toscana.cli;

public class Main {
    public static void main(String[] args) {
        CliProperties cliProperties = new CliProperties();
        ApiController apiController = new ApiController(cliProperties.getApiUrl());

        CliMain cliMain = new CliMain();
        cliMain.setApiController(apiController);
        cliMain.main(args);
    }
}
