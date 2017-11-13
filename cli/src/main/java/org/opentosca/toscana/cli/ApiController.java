package org.opentosca.toscana.cli;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.opentosca.toscana.cli.commands.Constants;
import org.opentosca.toscana.cli.restclient.RestService;
import org.opentosca.toscana.cli.restclient.model.Csar;
import org.opentosca.toscana.cli.restclient.model.Csars;
import org.opentosca.toscana.cli.restclient.model.CsarsResponse;
import org.opentosca.toscana.cli.restclient.model.Platform;
import org.opentosca.toscana.cli.restclient.model.Platforms;
import org.opentosca.toscana.cli.restclient.model.PlatformsResponse;
import org.opentosca.toscana.cli.restclient.model.Status;
import org.opentosca.toscana.cli.restclient.model.Transformation;
import org.opentosca.toscana.cli.restclient.model.TransformationArtifact;
import org.opentosca.toscana.cli.restclient.model.TransformationInput;
import org.opentosca.toscana.cli.restclient.model.TransformationInputs;
import org.opentosca.toscana.cli.restclient.model.TransformationLog;
import org.opentosca.toscana.cli.restclient.model.TransformationLogs;
import org.opentosca.toscana.cli.restclient.model.Transformations;
import org.opentosca.toscana.cli.restclient.model.TransformationsResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiController {

    private static final String API_URL = "http://localhost:8084/";
    private RestService service;
    private Constants con;

    /**
     * Constructor for the ApiController, parameters decide if there should be any output of information
     *
     * @param moreVerbose very detailed output
     * @param verbose     some output
     */
    public ApiController(boolean moreVerbose, boolean verbose) {
        con = new Constants();

        //starts the retrofit client with the chosen loglevel
        OkHttpClient client;
        HttpLoggingInterceptor interceptor;
        Retrofit retrofit;
        if (verbose) {
            interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        } else if (moreVerbose) {
            interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        } else {
            retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        service = retrofit.create(RestService.class);
    }

    /**
     * Calls the REST API to upload the CSAR, handles different response codes which are returned
     *
     * @param file CSAR Archive to upload
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String uploadCsar(File file) throws IOException {
        RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        Call<ResponseBody> uploadCsarCall = service.upCsar(file.getName(), part);
        Response<ResponseBody> response = uploadCsarCall.execute();

        if (response.code() == 200) {
            return con.CSAR_UPLOAD_SUCCESS;
        } else if (response.code() == 400) {
            if (response.errorBody().string() != null) {
                return con.CSAR_UPLOAD_ERROR400M + response.errorBody().string();
            } else {
                return con.CSAR_UPLOAD_ERROR400;
            }
        } else if (response.code() == 500) {
            return con.CSAR_UPLOAD_ERROR500;
        } else {
            return con.CSAR_UPLOAD_ERROR;
        }
    }

    /**
     * Calls the REST API and deletes the specified CSAR if it's available, handles different response
     * codes
     *
     * @param csar CSAR to delete from the Transformator
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String deleteCsar(String csar) throws IOException {
        Call<ResponseBody> deleteCsarCall = service.deleteCsar(csar);
        Response<ResponseBody> response = deleteCsarCall.execute();

        if (response.code() == 200) {
            return con.CSAR_DELETE_SUCCESS;
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.CSAR_DELETE_ERROR404M + response.errorBody().string();
            } else {
                return con.CSAR_DELETE_ERROR404;
            }
        } else if (response.code() == 500) {
            if (response.errorBody().string() != null) {
                return con.CSAR_DELETE_ERROR500M + response.errorBody().string();
            } else {
                return con.CSAR_DELETE_ERROR500;
            }
        } else {
            return con.CSAR_DELETE_ERROR;
        }
    }

    /**
     * Calls the REST API and lists all available CSARs, only handles code 200 or exception responses
     *
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String listCsar() throws IOException {
        String stringCsars = "";
        Call<CsarsResponse> csarsResponseCall = service.getCsars();
        Response<CsarsResponse> response = csarsResponseCall.execute();

        if (response.code() == 200) {
            if (response.body().getAllCsars() != null) {
                Csars aCsars = response.body().getAllCsars();
                List<Csar> csars = aCsars.getCsar();

                for (Csar c : csars) {
                    stringCsars += "\nName: " + c.getName();
                }
                return con.CSAR_LIST_SUCCESS + stringCsars;
            } else {
                return con.CSAR_LIST_EMPTY;
            }
        } else {
            return con.CSAR_LIST_ERROR;
        }
    }

    /**
     * Calls the REST API and prints detailed Information for the specified CSAR if it's available
     *
     * @param csarName Name of the CSAR which information should be shown
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String infoCsar(String csarName) throws IOException {
        Call<Csar> csarCall = service.getCsar(csarName);
        Response<Csar> response = csarCall.execute();

        if (response.code() == 200) {
            Csar csar = response.body();
            if (csar.getName() != null) {
                return con.CSAR_INFO_SUCCESS + csar.getName();
            } else {
                return con.CSAR_INFO_EMPTY;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.CSAR_INFO_ERROR404M + response.errorBody().string();
            } else {
                return con.CSAR_INFO_ERROR404;
            }
        } else {
            return con.CSAR_INFO_ERROR;
        }
    }

    /**
     * Calls the REST API and starts the Transformation, handles response codes
     *
     * @param csar CSAR for which a transformation should be started
     * @param plat platform for which a transformation should be started
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String startTransformation(String csar, String plat) throws IOException {
        Call<ResponseBody> startTransformationCall = service.createTransformation(csar, plat);
        Response<ResponseBody> response = startTransformationCall.execute();

        if (response.code() == 200) {
            return con.TRANSFORMATION_START_SUCCESS;
        } else if (response.code() == 400) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_START_ERROR400M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_START_ERROR400;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_START_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_START_ERROR404;
            }
        } else {
            return con.TRANSFORMATION_START_ERROR;
        }
    }

    /**
     * TODO: Implement functionality
     * Calls the REST API and stops the currently running Transformation if it's running
     *
     * @param csar CSAR to stop transformation for
     * @param plat platform to stop transformation for
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String stopTransformation(String csar, String plat) throws IOException {
        return con.TRANSFORMATION_STOP;
    }

    /**
     * Calls the REST API and deletes the specified Transformation, handles response codes
     *
     * @param csar CSAR for which transformation should be deleted
     * @param plat platform for which the transformation should be deleted
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String deleteTransformation(String csar, String plat) throws IOException {
        Call<ResponseBody> deleteTransformationCall = service.deleteTransformation(csar, plat);
        Response<ResponseBody> response = deleteTransformationCall.execute();

        if (response.code() == 200) {
            return con.TRANSFORMATION_DELETE_SUCCESS;
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_DELETE_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_DELETE_ERROR404;
            }
        } else if (response.code() == 500) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_DELETE_ERROR500M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_DELETE_ERROR500;
            }
        } else {
            return con.TRANSFORMATION_DELETE_ERROR;
        }
    }

    /**
     * Calls the REST API to download an Artifact for the specified finished Transformation,
     * handles response codes
     *
     * @param csar CSAR for which to download an Artifact
     * @param plat Platform for which the Artifact should be downloaded
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String downloadTransformation(String csar, String plat) throws IOException {
        Call<TransformationArtifact> artifactCall = service.getArtifact(csar, plat);
        Response<TransformationArtifact> response = artifactCall.execute();

        if (response.code() == 200) {
            TransformationArtifact artifact = response.body();
            if (artifact.getAccessUrl() != null) {
                return con.TRANSFORMATION_DOWNLOAD_SUCCESS + artifact.getAccessUrl();
            } else {
                return con.TRANSFORMATION_DOWNLOAD_EMPTY;
            }
        } else if (response.code() == 400) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_DOWNLOAD_ERROR400M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_DOWNLOAD_ERROR400;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_DOWNLOAD_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_DOWNLOAD_ERROR404;
            }
        } else {
            return con.TRANSFORMATION_DOWNLOAD_ERROR;
        }
    }

    /**
     * Calls the REST API and lists all available Transformations for the CSAR
     *
     * @param csar CSAR, for which transformations should be shown
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String listTransformation(String csar) throws IOException {
        String stringTransformations = "";
        Call<TransformationsResponse> transformationsResponseCall = service.getTransformations(csar);
        Response<TransformationsResponse> response = transformationsResponseCall.execute();

        if (response.code() == 200) {
            if (response.body().getAllTransformations() != null) {
                Transformations aTrans = response.body().getAllTransformations();
                List<Transformation> transformations = aTrans.getTransformation();

                for (Transformation t : transformations) {
                    stringTransformations += "\nPlatform: " + t.getPlatform();
                }
                return con.TRANSFORMATION_LIST_SUCCESS + csar + ": " + stringTransformations;
            } else {
                return con.TRANSFORMATION_LIST_EMPTY + csar + " is empty.";
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_LIST_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_LIST_ERROR404;
            }
        } else {
            return con.TRANSFORMATION_LIST_ERROR;
        }
    }

    /**
     * Calls the REST API and returns all Information about the Transformation
     *
     * @param csar CSAR, for which Transformation Info should be shown
     * @param plat Platform, for which Information should be shown
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String infoTransformation(String csar, String plat) throws IOException {
        Call<Transformation> transformationCall = service.getTransformation(csar, plat);
        Response<Transformation> response = transformationCall.execute();

        if (response.code() == 200) {
            Transformation transformation = response.body();
            if (transformation.getPlatform() != null) {
                return con.TRANSFORMATION_INFO_SUCCESS + csar + " on Platform: "
                    + plat + "\nPlatform: " + transformation.getPlatform()
                    + "\nProgress: " + transformation.getProgress() + "\nStatus: " + transformation.getStatus();
            } else {
                return con.TRANSFORMATION_INFO_EMPTY;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_INFO_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_INFO_ERROR404;
            }
        } else {
            return con.TRANSFORMATION_INFO_ERROR;
        }
    }

    /**
     * Calls the REST API and returns logs for the specified Transformation
     *
     * @param csar  CSAR for which a transformation is available
     * @param plat  Platform for which a transformation is available
     * @param start where to start with log output, default is start position 0
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String logsTransformation(String csar, String plat, int start) throws IOException {
        String stringLogs = "";
        Call<TransformationLogs> logsCall = service.getLogs(csar, plat, start);
        Response<TransformationLogs> response = logsCall.execute();

        if (response.code() == 200) {
            List<TransformationLog> transLogs;
            if (response.body().getLogs() != null) {
                transLogs = response.body().getLogs();
                if (transLogs.size() > 1) {
                    for (int i = start; i < transLogs.size(); i++) {
                        long time = transLogs.get(i).getTimestamp();
                        LocalDateTime timeStamp =
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
                                TimeZone.getDefault().toZoneId());

                        stringLogs += "\nTimestamp: " + timeStamp
                            + ", Message: " + transLogs.get(i).getMessage();
                    }
                    return con.TRANSFORMATION_LOGS_SUCCESS + stringLogs;
                } else {
                    return con.TRANSFORMATION_LOGS_EMPTY;
                }
            } else {
                return con.TRANSFORMATION_LOGS_EMPTY;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.TRANSFORMATION_LOGS_ERROR404M + response.errorBody().string();
            } else {
                return con.TRANSFORMATION_LOGS_ERROR404;
            }
        } else {
            return con.TRANSFORMATION_LOGS_ERROR;
        }
    }

    /**
     * Calls the REST API and shows every needed Input, that must be set before a transformation
     * can be started
     *
     * @param csar CSAR for which required inputs should be shown
     * @param plat Platform for which required inputs should be shown
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String inputList(String csar, String plat) throws IOException {
        String stringInputs = "";
        Call<TransformationInputs> inputsCall = service.getInputs(csar, plat);
        Response<TransformationInputs> response = inputsCall.execute();

        if (response.code() == 200) {
            if (response.body().getProperties() != null) {
                List<TransformationInput> transProperty = response.body().getProperties();

                for (TransformationInput p : transProperty) {
                    stringInputs += "\nKey: " + p.getKey()
                        + " Type: " + p.getType();
                }
                return con.INPUT_LIST_SUCCESS + stringInputs;
            } else {
                return con.INPUT_LIST_EMPTY;
            }
        } else if (response.code() == 400) {
            if (response.errorBody().string() != null) {
                return con.INPUT_LIST_ERROR400M + response.errorBody().string();
            } else {
                return con.INPUT_LIST_ERROR400;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.INPUT_LIST_ERROR404M + response.errorBody().string();
            } else {
                return con.INPUT_LIST_ERROR404;
            }
        } else {
            return con.INPUT_LIST_ERROR;
        }
    }

    /**
     * Calls the REST API, and trys to set the required Inputs. After they are set successfully
     * a transformation can be started
     *
     * @param csar   CSAR for which to set Inputs
     * @param plat   Platform for which to set Inputs
     * @param inputs the required inputs, format is key=value, = is not allowed as an identifier
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String placeInput(String csar, String plat, Map<String, String> inputs) throws IOException {
        Call<ResponseBody> inputsCall = service.setInputs(csar, plat, inputs);
        Response<ResponseBody> response = inputsCall.execute();

        if (response.code() == 200) {
            return con.INPUT_SET_SUCCESS;
        } else if (response.code() == 400) {
            if (response.errorBody().string() != null) {
                return con.INPUT_SET_ERROR400M + response.errorBody().string();
            } else {
                return con.INPUT_SET_ERROR400;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.INPUT_SET_ERROR404M + response.errorBody().string();
            } else {
                return con.INPUT_SET_ERROR404;
            }
        } else {
            return con.INPUT_SET_ERROR;
        }
    }

    /**
     * Calls the REST API and returns all Platforms, that are available for a transformation
     *
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String listPlatform() throws IOException {
        String stringPlatforms = "";
        Call<PlatformsResponse> platformsResponseCall = service.getPlatforms();
        Response<PlatformsResponse> response = platformsResponseCall.execute();

        if (response.code() == 200) {
            if (response.body().getAllPlatforms() != null) {
                Platforms plat = response.body().getAllPlatforms();
                List<Platform> platforms = plat.getPlatform();

                for (Platform p : platforms) {
                    stringPlatforms += "\nID: " + p.getId() + " Name: " + p.getName();
                }
                return con.PLATFORM_LIST_SUCCESS + stringPlatforms;
            } else {
                return con.PLATFORM_LIST_EMPTY;
            }
        } else {
            return con.PLATFORM_LIST_ERROR;
        }
    }

    /**
     * Calls the REST API and returns all Information about the Platform
     *
     * @param platform Platform for which all it's information should be shown
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String infoPlatform(String platform) throws IOException {
        Call<Platform> platformCall = service.getPlatform(platform);
        Response<Platform> response = platformCall.execute();
        Platform plat = response.body();

        if (response.code() == 200) {
            if (plat.getId() != null) {
                return con.PLATFORM_INFO_SUCCESS + plat.getId() + "\nName: " + plat.getName();
            } else {
                return con.PLATFORM_INFO_EMPTY;
            }
        } else if (response.code() == 404) {
            if (response.errorBody().string() != null) {
                return con.PLATFORM_INFO_ERROR404M + response.errorBody().string();
            } else {
                return con.PLATFORM_INFO_ERROR404;
            }
        } else {
            return con.PLATFORM_INFO_ERROR;
        }
    }

    /**
     * Calls the REST API and returns the current state of the system
     *
     * @return output for the CLI
     * @throws IOException if the responsebody is null
     */
    public String showStatus() throws IOException {
        Call<Status> statusCall = service.getSystemStatus();
        Response<Status> response = statusCall.execute();
        Status status = response.body();

        if (response.code() == 200) {
            if (status.getStatus() != null) {
                return con.STATUS_SUCCESS + "\nStatus: " + status.getStatus()
                    + "\nAvailable Storage: " + status.getAvailableStorage()
                    + "\nTotal Storage: " + status.getTotalStorage();
            } else {
                return con.STATUS_EMPTY;
            }
        } else {
            return con.STATUS_ERROR;
        }
    }
}
