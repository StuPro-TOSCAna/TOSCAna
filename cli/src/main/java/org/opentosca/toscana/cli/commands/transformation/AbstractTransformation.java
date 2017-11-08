package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 Abstract class to provide often used options and methods for the transformation package
 */
@Command(parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n")
public abstract class AbstractTransformation {

    @Option(names = {"-p", "--platform"}, paramLabel = Constants.PARAM_PLATFORM, description = "Platform for Transformation Start")
    private String platform = "";
    @Option(names = {"-t", "--transformation"}, paramLabel = "<CSAR/Platform>", description = "CSAR and Platform for the Transformation, Slash not allowed in the inputs. Correct format: -t csar/platform")
    private String transformation = "";
    @Option(names = {"-c", "--csar"}, paramLabel = Constants.PARAM_CSAR, description = "CSAR for Transformation Start")
    private String csar = "";
    @Option(names = {"-v", "--verbose"}, description = "Enable Info Level Process Output")
    private boolean showVerbose;
    @Option(names = {"-m", "--moreverbose"}, description = "Enable Debug Level Process Output")
    private boolean showMVerbose;

    private ApiController api;
    private Constants con;

    public ApiController startApi() {
        if (showMVerbose) {
            api = new ApiController(true, false);
            return api;
        } else if (showVerbose) {
            api = new ApiController(false, true);
            return api;
        } else {
            api = new ApiController(false, false);
            return api;
        }
    }

    /**
     Converts the transformation string into two separate strings that are used to call
     methods

     @param transformation provided Transformation String containing, CSAR and Platform
     @return the two separated String for CSAR and Platform
     @throws IOException if the transformation string contains more than one slash,
     only as separator allowed
     */
    private String[] inputTransformation(String transformation) throws IOException {
        if (transformation.matches(".*/.*/.*")) {
            throw new IOException("Please recheck your provided parameters, it must not contain more than one / separator");
        } else {
            String[] input = transformation.trim().split("/");
            if (input.length > 1) {
                return input;
            } else {
                throw new IOException(con.NOT_PROVIDED);
            }
        }
    }

    /**
     TODO: What does a throws IOException do here?
     Trys to convert the provided Information into CSAR and Platform Strings

     @throws IOException if no Information about CSAR and Platform are provided
     */
    String[] getInput() throws IOException {
        String[] in;
        con = new Constants();

        if (!transformation.isEmpty()) {
            in = inputTransformation(transformation);
        } else if (!csar.isEmpty() && !platform.isEmpty()) {
            in = new String[]{csar, platform};
        } else {
            throw new IOException(con.NOT_PROVIDED);
        }

        return in;
    }
}
