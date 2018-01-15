package org.opentosca.toscana.retrofit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.retrofit.model.Csar;
import org.opentosca.toscana.retrofit.model.Platform;
import org.opentosca.toscana.retrofit.model.ServerError;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.TransformationLogs;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformerStatus;
import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.model.embedded.PlatformResources;
import org.opentosca.toscana.retrofit.model.embedded.TransformationResources;
import org.opentosca.toscana.retrofit.model.hal.HALResource;
import org.opentosca.toscana.retrofit.service.TOSCAnaAPIService;
import org.opentosca.toscana.retrofit.util.LoggingMode;
import org.opentosca.toscana.retrofit.util.RetrofitLoggerWrapper;
import org.opentosca.toscana.retrofit.util.TOSCAnaServerException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TOSCAnaAPI {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(TOSCAnaAPI.class);
    private static final MediaType UPLOAD_MIME_TYPE = MediaType.parse("multipart/form-data");

    private String url;
    private Retrofit retrofit;
    private TOSCAnaAPIService apiService;

    private ObjectMapper objectMapper;

    public TOSCAnaAPI(String url) {
        this(url, LoggingMode.OFF);
        logger.setLevel(Level.OFF);
    }

    public TOSCAnaAPI(String url, LoggingMode mode) {
        this.url = url;
        setLoggingMode(mode);

        //Build the logging interceptor
        logger.debug("Creating Http Logging Interceptor with mode {}", mode.name());
        HttpLoggingInterceptor interceptor
            = new HttpLoggingInterceptor(new RetrofitLoggerWrapper(url));
        interceptor.setLevel(mode.getLevel());

        //Create a Http Client with the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        //Build the retrofit instance
        logger.debug("Creating Retrofit instance");
        this.retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
        //Create the service
        logger.debug("Creating TOSCAna API service");
        this.apiService = retrofit.create(TOSCAnaAPIService.class);

        //Create the Jackson Object Mapper used by this class
        this.objectMapper = new ObjectMapper();
    }

    public void setLoggingMode(LoggingMode mode) {
        logger.setLevel(Level.valueOf(mode.name()));
    }

    private <E> E performCall(Call<E> call)
        throws TOSCAnaServerException, IOException {
        logger.debug("Performing call to {}", call.request().url().toString());
        Response<E> response = call.execute();

        if (response.isSuccessful()) {
            logger.debug("Execution of the call was successful");
            return response.body();
        } else {
            logger.debug("Execution of the call was not successful");
            throwToscanaException(call, response);
            //Dead code (used to prevent "Missing return statement" compilation error
            return null;
        }
    }

    public HALResource getAPIRoot()
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getAPIRoot());
    }

    public TransformerStatus getServerStatus()
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getTransformerHealth());
    }

    public Map<String, Object> getTransformerMetrics()
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getTransformerMetrics());
    }

    public PlatformResources getPlatforms()
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getPlatforms());
    }

    public Platform getPlatformDetails(String name)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getPlatformDetails(name));
    }

    public ResponseBody deleteCsar(String name)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.deleteCsar(name));
    }

    public CsarResources getCsars()
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getCsars());
    }

    public Csar getCsarDetails(String name)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getCsarDetails(name));
    }

    public TransformationResources getTransformations(String csarName)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getTransformationsForCsar(csarName));
    }

    public ResponseBody deleteTransformation(String csarName, String platform)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.deleteTransformation(csarName, platform));
    }

    public Transformation getTransformation(String csarName, String platform)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getTransformationDetails(csarName, platform));
    }

    public ResponseBody createTransformation(String csarName, String platform)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.createTransformation(csarName, platform));
    }

    public ResponseBody startTransformation(String csarName, String platform)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.startTransformation(csarName, platform));
    }

    public TransformationLogs getLogs(String csarName, String platform, long start)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getTransformationLogs(csarName, platform, start));
    }

    public TransformationProperties getProperties(String csarName, String platform)
        throws IOException, TOSCAnaServerException {
        return performCall(apiService.getProperties(csarName, platform));
    }

    public Map<String, Boolean> updateProperties(
        String csarName,
        String platform,
        TransformationProperties props
    ) throws IOException, TOSCAnaServerException {
        Call<ResponseBody> call = apiService.updateProperties(csarName, platform, props);
        Response<ResponseBody> response = call.execute();
        if (response.code() == 400) {
            return objectMapper.readValue(response.errorBody().string(), Map.class);
        } else if (response.code() == 406) {
            // input validation failed return map with a boolean if property value for given key is correct
            return (Map<String, Boolean>) objectMapper.readValue(response.errorBody().string(), Map.class).get("valid_inputs");
        } else if (response.isSuccessful()) {
            Map<String, Boolean> result = new HashMap<>();
            props.getProperties()
                .forEach(transformationProperty -> result.put(transformationProperty.getKey(), true));
            return result;
        }
        throwToscanaException(call, response);
        return null;
    }

    public byte[] downloadArtifactAsBytes(String csarName,
                                          String platform)
        throws IOException, TOSCAnaServerException {
        ResponseBody body = performCall(apiService.getArtifact(csarName, platform));
        return body.bytes();
    }

    public InputStream downloadArtifactAsStream(String csarName,
                                                String platform)
        throws IOException, TOSCAnaServerException {
        ResponseBody body = performCall(apiService.getArtifact(csarName, platform));
        return body.byteStream();
    }

    public String getArtifactDownloadUrl(String csarName, String platform) {
        return apiService.getArtifact(csarName, platform).request().url().toString();
    }

    public void uploadCsar(String name, File path)
        throws IOException, TOSCAnaServerException {
        String n = path.getName();
        RequestBody fileBody = RequestBody.create(UPLOAD_MIME_TYPE, path);
        uploadInternal(name, n, fileBody);
    }

    public void uploadCsar(String name, byte[] data)
        throws IOException, TOSCAnaServerException {
        RequestBody body = RequestBody.create(UPLOAD_MIME_TYPE, data);
        uploadInternal(name, name + ".csar", body);
    }

    private void uploadInternal(String name, String n, RequestBody fileBody)
        throws TOSCAnaServerException, IOException {
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", n, fileBody);
        performCall(apiService.upload(part, name));
    }

    public String getUrl() {
        return url;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private <E> void throwToscanaException(
        Call<E> call, Response<E> response
    ) throws TOSCAnaServerException, IOException {
        ResponseBody errorBody = response.errorBody();
        TOSCAnaServerException exception = new TOSCAnaServerException(
            "Execution of HTTP call " + call.request().url() + " failed",
            errorBody == null || errorBody.contentLength() == 0 ?
                null : objectMapper.readValue(errorBody.string(), ServerError.class),
            response.code()
        );
        logger.debug("Throwing Exception", exception);
        exception.printLog();
        throw exception;
    }
}
