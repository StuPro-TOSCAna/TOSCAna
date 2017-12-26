package org.opentosca.toscana.cli.commands.transformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opentosca.toscana.cli.ApiController;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "input",
    description = {"Required Inputs for the specified Transformation"},
    customSynopsis = {"@|bold toscana input|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana input|@ @|yellow -t=<csar/platform>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana input|@ @|yellow -c=<name>|@ @|yellow -p=<name>|@ @|yellow <key=value>|@ [@|yellow -mv|@]",
        "   or: @|bold toscana input|@ @|yellow -t=<csar/platform>|@ @|yellow -f=<path>|@ [@|yellow -mv|@]%n"})
public class TransformationInput extends AbstractTransformation {

    @Option(names = {"-f", "--file"}, paramLabel = "Input File", description = "Input File to provide")
    private File inputFile;

    @Parameters(arity = "0..*", paramLabel = "<Key=Value>", description = "Input to provide")
    private List<String> input;

    /**
     shows required inputs to start a transformation, or set's them
     */
    public TransformationInput() {

    }

    @Override
    protected String performCall(ApiController ap, String[] ent) {
        return null;
    }

    @Override
    public void run() {

        try {
            final String[] entered = getInput();
            if (entered != null) {
                if (input != null && !input.isEmpty()) {
                    System.out.println(api.placeInput(entered[0], entered[1], inputManual(input)));
                } else if (inputFile != null) {
                    System.out.println(api.placeInput(entered[0], entered[1], inputFile(inputFile)));
                } else {
                    System.out.println(api.inputList(entered[0], entered[1]));
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     Method to return Inputs that are set manual in the CLI

     @param list inputs that are made through the CLI
     @return returns all manual set inputs
     */
    public Map<String, String> inputManual(List<String> list) throws IOException {
        Map<String, String> map = new HashMap<>();

        for (String in : list) {
            if (in.matches(".*=.*=.*")) {
                throw new IllegalArgumentException("Please recheck your provided inputs, it must not contain more than one = separator");
            } else {
                String[] inputArray = in.split("=");
                if (inputArray.length > 1) {
                    map.put(inputArray[0], inputArray[1]);
                } else {
                    throw new IllegalArgumentException("Please add required Inputs as key=value, = is not allowed in those Information");
                }
            }
        }
        return map;
    }

    /**
     Method to return Inputs that are provided through a file

     @param file containing the required Inputs, Properties like format
     @return returns all inputs that are provided in the file
     @throws IOException if the file is not found
     */
    public Map<String, String> inputFile(File file) throws IOException {
        Map<String, String> map = new HashMap<>();
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(file);
        properties.load(inputStream);
        inputStream.close();
        Enumeration pro = properties.propertyNames();

        while (pro.hasMoreElements()) {
            String key = pro.nextElement().toString();
            String value = properties.getProperty(key);
            map.put(key, value);
        }
        return map;
    }
}
