package org.opentosca.toscana.cli.commands.transformation;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;

import static picocli.CommandLine.usage;

@Command(name = "transformation",
    customSynopsis = "@|bold toscana transformation|@ [@|yellow <subcommand>|@] [@|yellow -mv|@]%n",
    commandListHeading = "%nAvailable Subcommands are:%n",
    subcommands = {TransformationDelete.class,
        TransformationDownload.class,
        TransformationInfo.class,
        TransformationLogs.class,
        TransformationStart.class,
        TransformationStop.class,
        TransformationList.class})
public class ToscanaTransformation extends AbstractTransformation {

    /**
     shows the transformation usage and available subcommands
     */
    public ToscanaTransformation() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return null;
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}
