package org.opentosca.toscana.cli.commands;

import picocli.CommandLine.Command;

/**
 Abstract class to provide often used Options and the ApiController initialization
 */
@Command(//parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n")
public abstract class AbstractCommand extends AbstractApiCall {
}
