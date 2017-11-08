package org.opentosca.toscana.cli.commands.csar;

import org.opentosca.toscana.cli.commands.AbstractCommand;

import picocli.CommandLine.Command;

import static picocli.CommandLine.usage;

@Command(name = "csar",
    description = {"CSAR Overview"},
    customSynopsis = "@|bold toscana csar|@ [@|yellow <subcommand>|@] [@|yellow -mv|@]%n",
    commandListHeading = "%nAvailable Subcommands are:%n",
    subcommands = {CsarDelete.class,
        CsarInfo.class,
        CsarList.class,
        CsarUpload.class})
public class ToscanaCsar extends AbstractCommand implements Runnable {

    /**
     Show's the usage of the CSAR command and available subcommands
     */
    public ToscanaCsar() {
    }

    @Override
    public void run() {
        usage(this, System.out);
    }
}
