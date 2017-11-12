package org.opentosca.toscana.cli.commands.csar;

import java.io.File;
import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractCommand;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "upload",
    description = {"Upload the specified CSAR Archive for Transformation"},
    customSynopsis = "@|bold toscana csar upload|@ @|yellow -f=<path>|@ [@|yellow -mv|@]%n",
    optionListHeading = "%nOptions:%n")
public class CsarUpload extends AbstractCommand implements Runnable {

    @Option(names = {"-f", "--file"}, required = true, paramLabel = Constants.PARAM_CSAR, description = "CSAR to upload")
    private File csar;

    /**
     Get's called if a CSAR should be uploaded
     */
    public CsarUpload() {
    }

    @Override
    public void run() {
        ApiController api = startApi();

        try {
            System.out.println(api.uploadCsar(csar));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
