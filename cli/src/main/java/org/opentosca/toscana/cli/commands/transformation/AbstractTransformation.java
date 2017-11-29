package org.opentosca.toscana.cli.commands.transformation;

import java.io.IOException;

import org.opentosca.toscana.cli.ApiController;
import org.opentosca.toscana.cli.commands.AbstractApiCall;
import org.opentosca.toscana.cli.commands.Constants;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 Abstract class to provide often used options and methods for the transformation package
 */
@Command(//parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    commandListHeading = "%nCommands:%n")
public abstract class AbstractTransformation extends AbstractApiCall {

    @Option(names = {"-p", "--platform"}, paramLabel = Constants.PARAM_PLATFORM, description = "Platform for Transformation")
    private String platform;
    @Option(names = {"-t", "--transformation"}, paramLabel = "<CSAR/Platform>", description = "CSAR and Platform for the Transformation, Slash not allowed in the inputs. Correct format: -t csar/platform")
    private String transformation;
    @Option(names = {"-c", "--csar"}, paramLabel = Constants.PARAM_CSAR, description = "CSAR for Transformation")
    private String csar;

    AbstractTransformation() {
    }

    protected abstract String performCall(ApiController ap, String[] ent);

    @Override
    public void run() {
        try {
            System.out.println(callApi());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     Converts the transformation string into two separate strings that are used to call methods

     @param transformation provided Transformation String containing, CSAR and Platform
     @return the two separated String for CSAR and Platform only as separator allowed
     */
    private String[] inputTransformation(String transformation) throws IOException {
        String[] input;
        if (transformation.matches(".*\\/.*\\/.*")) {
            throw new IllegalArgumentException("Please recheck your provided parameters, it must not contain more than one / separator");
        } else {
            input = transformation.trim().split("/");
            if (input.length < 1) {
                throw new IllegalArgumentException(Constants.NOT_PROVIDED);
            }
        }
        return input;
    }

    /**
     Trys to convert the provided Information into CSAR and Platform Strings
     */
    String[] getInput() throws IOException {
        String[] in;
        if (transformation != null) {
            in = inputTransformation(transformation);
        } else if (csar != null && platform != null) {
            in = new String[]{csar, platform};
        } else if (csar != null) {
            in = new String[]{csar};
        } else {
            throw new IllegalArgumentException(Constants.NOT_PROVIDED);
        }
        return in;
    }

    /**
     Method to call the CLI with the specified performCall methods of the calling classes

     @return the output for the CLI
     @throws IOException if something in the Input was not correct
     */
    private String callApi() throws IOException {
        final String[] entered = getInput();
        String response = "";

        if (entered != null) {
            response = performCall(api, entered);
        }
        return response;
    }
}
