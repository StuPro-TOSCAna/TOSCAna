package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.cli.commands.Constants;
import org.opentosca.toscana.retrofit.TOSCAnaAPI;
import org.opentosca.toscana.retrofit.model.Csar;
import org.opentosca.toscana.retrofit.model.LogEntry;
import org.opentosca.toscana.retrofit.model.Platform;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.TransformationLogs;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformationProperty;
import org.opentosca.toscana.retrofit.model.TransformerStatus;
import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.model.embedded.PlatformResources;
import org.opentosca.toscana.retrofit.model.embedded.TransformationResources;
import org.opentosca.toscana.retrofit.util.LoggingMode;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

public class ApiController {

    private Constants con;
    private TOSCAnaAPI toscAnaAPI;

    /**
     Constructor for the ApiController, parameters decide if there should be any output of information

     @param moreVerbose very detailed output
     @param verbose     some output
     */
    public ApiController(boolean moreVerbose, boolean verbose) {
        con = new Constants();
        CliProperties prop = new CliProperties();
        final String API_URL = prop.getApiUrl();

        //starts the retrofit client with the chosen loglevel
        try {
            if (verbose) {
                toscAnaAPI = new TOSCAnaAPI(API_URL, LoggingMode.MEDIUM);
            } else if (moreVerbose) {
                toscAnaAPI = new TOSCAnaAPI(API_URL, LoggingMode.HIGH);
            } else {
                toscAnaAPI = new TOSCAnaAPI(API_URL);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     Calls the REST API to upload the CSAR, handles different response codes which are returned

     @param file CSAR Archive to upload
     @return output for the CLI
     */
    public void uploadCsar(File file) {
        try {
            toscAnaAPI.uploadCsar(file.getName(), file);
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar '%s':%n'%s'", file.getName(), e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while uploading Csar '%s':%n%s '%s'", file.getName(), e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
    }

    /**
     Calls the REST API and deletes the specified CSAR if it's available, handles different response codes

     @param csar CSAR to delete from the Transformator
     @return output for the CLI
     */
    public void deleteCsar(String csar) {
        try {
            toscAnaAPI.deleteCsar(csar);
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while deleting Csar '%s':%n'%s'", csar, e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while deleting Csar '%s':%n%s '%s'", csar, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
    }

    /**
     Calls the REST API and lists all available CSARs, only handles code 200 or exception responses

     @return output for the CLI
     */
    public String listCsar() {
        CsarResources csarList;
        StringBuilder stringCsars = new StringBuilder();
        try {
            csarList = toscAnaAPI.getCsars();
            List<Csar> list = csarList.getContent();

            for (Csar c : list) {
                stringCsars.append("\n").append(c.getName());
            }
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringCsars.toString();
    }

    /**
     Calls the REST API and prints detailed Information for the specified CSAR if it's available

     @param csarName Name of the CSAR which information should be shown
     @return output for the CLI
     */
    public String infoCsar(String csarName) {
        String cName = "";
        Csar csar;
        try {
            csar = toscAnaAPI.getCsarDetails(csarName);
            cName = csar.getName();
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return cName;
    }

    /**
     Calls the REST API and starts the Transformation, handles response codes

     @param csar CSAR for which a transformation should be started
     @param plat platform for which a transformation should be started
     @return output for the CLI
     */
    public void startTransformation(String csar, String plat) {
        try {
            toscAnaAPI.createTransformation(csar, plat);
            launchTransformation(csar, plat);
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while creating Transformation:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while creating Transformation:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
    }

    private void launchTransformation(String csar, String platform) {
        try {
            toscAnaAPI.startTransformation(csar, platform);
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while starting Transformation:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while starting Transformation:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
    }

    /**
     TODO: Implement functionality Calls the REST API and stops the currently running Transformation if it's running

     @param csar CSAR to stop transformation for
     @param plat platform to stop transformation for
     @return output for the CLI
     */
    public String stopTransformation(String csar, String plat) {
        return con.TRANSFORMATION_STOP;
    }

    /**
     Calls the REST API and deletes the specified Transformation, handles response codes

     @param csar CSAR for which transformation should be deleted
     @param plat platform for which the transformation should be deleted
     @return output for the CLI
     */
    public void deleteTransformation(String csar, String plat) {
        try {
            toscAnaAPI.deleteTransformation(csar, plat);
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while deleting Csar '%s' on Platform '%s':%n'%s'", csar, plat, e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while deleting Csar '%s':%n%s '%s'", csar, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
    }

    /**
     Calls the REST API to download an Artifact for the specified finished Transformation, handles response codes

     @param csar CSAR for which to download an Artifact
     @param plat Platform for which the Artifact should be downloaded
     @return output for the CLI
     */
    public String downloadTransformation(String csar, String plat) {
        String downloadUrl = "";
        downloadUrl = toscAnaAPI.getArtifactDownloadUrl(csar, plat);

        return downloadUrl;
    }

    /**
     Calls the REST API and lists all available Transformations for the CSAR

     @param csar CSAR, for which transformations should be shown
     @return output for the CLI
     */
    public String listTransformation(String csar) {
        TransformationResources transformationList;
        StringBuilder stringTransformations = new StringBuilder();
        try {
            transformationList = toscAnaAPI.getTransformations(csar);
            List<Transformation> list = transformationList.getContent();

            for (Transformation t : list) {
                stringTransformations.append("\n").append(t.getPlatform());
            }
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Transformation List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while loading Transformation List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringTransformations.toString();
    }

    /**
     Calls the REST API and returns all Information about the Transformation

     @param csar CSAR, for which Transformation Info should be shown
     @param plat Platform, for which Information should be shown
     @return output for the CLI
     */
    public String infoTransformation(String csar, String plat) {
        Transformation transformation;
        StringBuilder stringTransformation = new StringBuilder();
        try {
            transformation = toscAnaAPI.getTransformation(csar, plat);

            stringTransformation.append(transformation.getPlatform()).append(", ").append(transformation.getProgress()).append(", ").append(transformation.getStatus());
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }

        return stringTransformation.toString();
    }

    /**
     Calls the REST API and returns logs for the specified Transformation

     @param csar  CSAR for which a transformation is available
     @param plat  Platform for which a transformation is available
     @param start where to start with log output, default is start position 0
     @return output for the CLI
     */
    public String logsTransformation(String csar, String plat, int start) {
        TransformationLogs logsList;
        StringBuilder stringLogs = new StringBuilder();
        try {
            logsList = toscAnaAPI.getLogs(csar, plat, start);
            List<LogEntry> list = logsList.getLogEntries();

            for (LogEntry l : list) {
                stringLogs.append(l.getTimestamp()).append(", ").append(l.getMessage()).append(", ").append(l.getLevel());
            }
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringLogs.toString();
    }

    /**
     Calls the REST API and shows every needed Input, that must be set before a transformation can be started

     @param csar CSAR for which required inputs should be shown
     @param plat Platform for which required inputs should be shown
     @return output for the CLI
     */
    public String inputList(String csar, String plat) {
        TransformationProperties propertiesList;
        StringBuilder stringProperties = new StringBuilder();
        try {
            propertiesList = toscAnaAPI.getProperties(csar, plat);
            List<TransformationProperty> list = propertiesList.getProperties();

            for (TransformationProperty p : list) {
                stringProperties.append(p.getKey()).append(", ").append(p.getValue()).append(", ").append(p.getDescription());
            }
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringProperties.toString();
    }

    /**
     Calls the REST API, and trys to set the required Inputs. After they are set successfully a transformation can be
     started

     @param csar   CSAR for which to set Inputs
     @param plat   Platform for which to set Inputs
     @param inputs the required inputs, format is key=value, = is not allowed as an identifier
     @return output for the CLI
     */
    public String placeInput(String csar, String plat, Map<String, String> inputs) {
        return null;
        //TODO
    }

    /**
     Calls the REST API and returns all Platforms, that are available for a transformation

     @return output for the CLI
     */
    public String listPlatform() {
        PlatformResources platformList;
        StringBuilder stringPlatforms = new StringBuilder();
        try {
            platformList = toscAnaAPI.getPlatforms();
            List<Platform> list = platformList.getContent();

            for (Platform p : list) {
                stringPlatforms.append("\n").append(p.getId()).append(", ").append(p.getName());
            }
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong while loading Platform List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringPlatforms.toString();
    }

    /**
     Calls the REST API and returns all Information about the Platform

     @param plat Platform for which all it's information should be shown
     @return output for the CLI
     */
    public String infoPlatform(String plat) {
        Platform platform;
        StringBuilder stringPlatform = new StringBuilder();
        try {
            platform = toscAnaAPI.getPlatformDetails(plat);

            stringPlatform.append(platform.getId()).append(", ").append(platform.getName());
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }

        return stringPlatform.toString();
    }

    /**
     Calls the REST API and returns the current state of the system

     @return output for the CLI
     */
    public String showStatus() {
        TransformerStatus status;
        StringBuilder stringStatus = new StringBuilder();
        try {
            status = toscAnaAPI.getServerStatus();

            stringStatus.append(status.getStatus()).append(", ").append(status.getFileSystemHealth()).append(", ").append(status.getTransformerHealth());
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }

        return stringStatus.toString();
    }

    public String showMetrics() {
        Map<String, Object> status;
        StringBuilder stringMetrics = new StringBuilder();
        try {
            status = toscAnaAPI.getTransformerMetrics();

            stringMetrics.append(status.keySet());
        } catch (IOException e) {
            System.err.println(String.format("Something went wrong while loading Csar List:%n'%s'", e.getMessage()));
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format("Something went wrong loading Csar List:%n%s '%s'", e.getStatusCode(), e.getErrorResponse().getMessage()));
        }

        return stringMetrics.toString();
    }
}
