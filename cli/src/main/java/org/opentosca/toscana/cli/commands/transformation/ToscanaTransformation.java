package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

import static picocli.CommandLine.usage;

@Command(name = "transformation",
    description = {"Transformation Overview"},
    customSynopsis = "@|bold toscana transformation|@ [@|yellow <subcommand>|@] [@|yellow -mv|@]%n",
    commandListHeading = "%nAvailable Subcommands are:%n",
    subcommands = {TransformationDelete.class,
        TransformationDownload.class,
        TransformationInfo.class,
        TransformationLogs.class,
        TransformationStart.class,
        TransformationStop.class,
        TransformationList.class})
public class ToscanaTransformation extends AbstractCommand implements Runnable {

    /**
     shows the transformation usage and available subcommands
     */
    public ToscanaTransformation() {
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}
