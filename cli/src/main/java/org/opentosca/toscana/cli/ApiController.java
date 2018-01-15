package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
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

import static org.opentosca.toscana.cli.commands.Constants.SOMETHING_WRONG;

public class ApiController {

    private TOSCAnaAPI toscAnaAPI;

    /**
     Constructor for the ApiController, parameters decide if there should be any output of information
     */
    public ApiController(String apiUrl) {
        toscAnaAPI = new TOSCAnaAPI(apiUrl);
    }

    /**
     Calls the REST API to upload the CSAR, handles different response codes which are returned

     @param file CSAR Archive to upload
     @return output for the CLI
     */
    public String uploadCsar(File file) {
        try {
            toscAnaAPI.uploadCsar(file.getName(), file);
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_UPLOAD_IO_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            e.printLog();
        }
        return "";
    }

    /**
     Calls the REST API and deletes the specified CSAR if it's available, handles different response codes

     @param csar CSAR to delete from the Transformator
     @return output for the CLI
     */
    public String deleteCsar(String csar) {
        try {
            toscAnaAPI.deleteCsar(csar);
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_DELETE_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_DELETE_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return "";
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
            if (list != null) {
                for (Csar c : list) {
                    stringCsars.append(c.getName()).append("\n");
                }
                stringCsars.delete(stringCsars.length() - 1, stringCsars.length());
            } else {
                return Constants.CSAR_LIST_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_LIST_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_LIST_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (csar != null) {
                cName = csar.getName();
            } else {
                return Constants.CSAR_INFO_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_INFO_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.CSAR_INFO_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return cName;
    }

    /**
     Calls the REST API and starts the Transformation, handles response codes

     @param csar CSAR for which a transformation should be started
     @param plat platform for which a transformation should be started
     @return output for the CLI
     */
    public String startTransformation(String csar, String plat) {
        try {
            toscAnaAPI.createTransformation(csar, plat);
            launchTransformation(csar, plat);
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_CREATE_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_CREATE_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return "";
    }

    /**
     Calls the REST API and launches the Transformation, handles response codes

     @param csar     CSAR for which a transformation should be launched
     @param platform platform for which a transformation should be launched
     @return output for the CLI
     */
    private String launchTransformation(String csar, String platform) {
        try {
            toscAnaAPI.startTransformation(csar, platform);
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_START_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_START_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return "";
    }

    /**
     TODO: Implement functionality Calls the REST API and stops the currently running Transformation if it's running

     @param csar CSAR to stop transformation for
     @param plat platform to stop transformation for
     @return output for the CLI
     */
    public String stopTransformation(String csar, String plat) {
        return Constants.TRANSFORMATION_STOP;
    }

    /**
     Calls the REST API and deletes the specified Transformation, handles response codes

     @param csar CSAR for which transformation should be deleted
     @param plat platform for which the transformation should be deleted
     @return output for the CLI
     */
    public String deleteTransformation(String csar, String plat) {
        try {
            toscAnaAPI.deleteTransformation(csar, plat);
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_DELETE_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_DELETE_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return "";
    }

    /**
     Calls the REST API to download an Artifact for the specified finished Transformation, handles response codes

     @param csar CSAR for which to download an Artifact
     @param plat Platform for which the Artifact should be downloaded
     @return output for the CLI
     */
    public String downloadTransformationUrl(String csar, String plat) {
        return toscAnaAPI.getArtifactDownloadUrl(csar, plat);
    }

    /**
     Calls the REST API to download an Artifact for the specified finished Transformation, output is stored at
     specified location

     @param csar CSAR for which to download an Artifact
     @param plat Platform for which the Artifact should be downloaded
     @return output for the CLI
     */
    public String downloadTransformationStream(String csar, String plat, File destinationFile) {
        try {
            InputStream input = toscAnaAPI.downloadArtifactAsStream(csar, plat);
            java.nio.file.Files.copy(input, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            input.close();
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_DOWNLOAD_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_DOWNLOAD_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return "";
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
            if (list != null) {
                for (Transformation t : list) {
                    stringTransformations.append(t.getPlatform()).append("\n");
                }
                stringTransformations.delete(stringTransformations.length() - 1, stringTransformations.length());
            } else {
                return Constants.TRANSFORMATION_LIST_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_LIST_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_LIST_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (transformation != null) {
                stringTransformation.append(transformation.getPlatform()).append(", ")
                    .append(transformation.getProgress()).append(", ")
                    .append(transformation.getStatus());
            } else {
                return Constants.TRANSFORMATION_INFO_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_INFO_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_INFO_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (list != null) {
                for (LogEntry l : list) {
                    stringLogs.append(l.getTimestamp()).append(", ")
                        .append(l.getMessage()).append(", ")
                        .append(l.getLevel()).append(", ");
                }
                stringLogs.delete(stringLogs.length() - 2, stringLogs.length());
            } else {
                return Constants.TRANSFORMATION_LOGS_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_LOGS_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.TRANSFORMATION_LOGS_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (list != null) {
                for (TransformationProperty p : list) {
                    stringProperties.append(p.getKey()).append(", ")
                        .append(p.getType()).append(", ")
                        .append(p.isRequired()).append(", ")
                        .append(p.getDescription()).append("\n");
                }
                stringProperties.delete(stringProperties.length() - 3, stringProperties.length());
            } else {
                return Constants.INPUT_LIST_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.INPUT_LIST_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.INPUT_LIST_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
        //prepare Inputs which should be updated
        List<TransformationProperty> properties = new ArrayList<>();
        for (Map.Entry<String, String> mapEntry : inputs.entrySet()) {
            TransformationProperty p = new TransformationProperty();
            p.setKey(mapEntry.getKey());
            p.setValue(mapEntry.getValue());
            properties.add(p);
        }
        TransformationProperties sendProp = new TransformationProperties(properties);

        //get Return of the update if it was successfull
        Map<String, Boolean> propertiesReturn;
        StringBuilder stringProperties = new StringBuilder();
        try {
            propertiesReturn = toscAnaAPI.updateProperties(csar, plat, sendProp);
            if (propertiesReturn != null) {
                for (String s : propertiesReturn.keySet()) {
                    stringProperties.append(s).append(" ")
                        .append(propertiesReturn.get(s));
                }
            } else {
                return Constants.INPUT_SET_ERROR;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.INPUT_SET_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.INPUT_SET_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
        }
        return stringProperties.toString();
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
            if (list != null) {
                for (Platform p : list) {
                    stringPlatforms.append(p.getId()).append(", ")
                        .append(p.getName()).append("\n");
                }
                stringPlatforms.delete(stringPlatforms.length() - 1, stringPlatforms.length());
            } else {
                return Constants.PLATFORM_LIST_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.PLATFORM_LIST_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.PLATFORM_LIST_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (platform != null) {
                stringPlatform.append(platform.getId()).append(", ")
                    .append(platform.getName());
            } else {
                return Constants.PLATFORM_INFO_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.PLATFORM_INFO_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.PLATFORM_INFO_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
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
            if (status != null) {
                stringStatus.append(status.getStatus()).append(", free: ")
                    .append(status.getFileSystemHealth().getFreeBytes());
            } else {
                return Constants.STATUS_EMPTY;
            }
        } catch (IOException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.STATUS_ERROR
                + Constants.ERROR_PLACEHOLDER, e.getMessage()));
            e.printStackTrace();
            System.exit(1);
        } catch (TOSCAnaServerException e) {
            System.err.println(String.format(SOMETHING_WRONG + Constants.STATUS_ERROR
                + Constants.SERVER_ERROR_PLACEHOLDER, e.getStatusCode(), e.getErrorResponse().getMessage()));
            System.exit(1);
        }
        return stringStatus.toString();
    }

    public void setLoggingMode(LoggingMode loggingMode) {
        toscAnaAPI.setLoggingMode(loggingMode);
    }
}
