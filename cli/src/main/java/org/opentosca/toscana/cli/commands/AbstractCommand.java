package org.opentosca.toscana.cli.commands;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.csar.CsarList;
import org.opentosca.toscana.cli.commands.platform.PlatformList;

import picocli.CommandLine.Command;

/**
 Abstract class to provide often used Options and the ApiController initialization
 */
@Command(//parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n")
public abstract class AbstractCommand extends AbstractApiCall {

    private ApiController api;

    public AbstractCommand() {
        api = getApi();
    }

    @Override
    public void run() {
        System.out.println(callApi(this.getClass()));
    }

    String callApi(Class call) {
        String response = "";
        if (call == CsarList.class) {
            response = api.listCsar();
        } else if (call == PlatformList.class) {
            response = api.listPlatform();
        }
        return response;
    }
}
