package org.opentosca.toscana.cli.restclient;

import java.util.Map;

import org.opentosca.toscana.cli.restclient.model.Csar;
import org.opentosca.toscana.cli.restclient.model.CsarsResponse;
import org.opentosca.toscana.cli.restclient.model.Platform;
import org.opentosca.toscana.cli.restclient.model.PlatformsResponse;
import org.opentosca.toscana.cli.restclient.model.Status;
import org.opentosca.toscana.cli.restclient.model.Transformation;
import org.opentosca.toscana.cli.restclient.model.TransformationArtifact;
import org.opentosca.toscana.cli.restclient.model.TransformationInputs;
import org.opentosca.toscana.cli.restclient.model.TransformationLogs;
import org.opentosca.toscana.cli.restclient.model.TransformationsResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestService {

    @GET("api/status")
    Call<Status> getSystemStatus();

    @GET("api/platforms")
    Call<PlatformsResponse> getPlatforms();

    @GET("api/platforms/{platform}")
    Call<Platform> getPlatform(@Path("platform") String platform);

    @GET("api/csars")
    Call<CsarsResponse> getCsars();

    @DELETE("api/csars/{csar}/delete")
    Call<ResponseBody> deleteCsar(@Path("csar") String csar);

    @GET("api/csars/{csar}")
    Call<Csar> getCsar(@Path("csar") String csar);

    @Multipart
    @POST("api/csars/{csar}")
    Call<ResponseBody> upCsar(@Path("csar") String csar, @Part MultipartBody.Part body);

    @GET("api/csars/{csar}/transformations")
    Call<TransformationsResponse> getTransformations(@Path("csar") String csar);

    @GET("api/csars/{csar}/transformations/{platform}")
    Call<Transformation> getTransformation(@Path("csar") String csar, @Path("platform") String platform);

    @POST("api/csars/{csar}/transformations/{platform}/create")
    Call<ResponseBody> createTransformation(@Path("csar") String csar, @Path("platform") String platform);

    @DELETE("api/csars/{csar}/transformations/{platform}/delete")
    Call<ResponseBody> deleteTransformation(@Path("csar") String csar, @Path("platform") String platform);

    @GET("api/csars/{csar}/transformations/{platform}/logs")
    Call<TransformationLogs> getLogs(@Path("csar") String csar, @Path("platform") String platform, @Query("start") int start);

    @GET("api/csars/{csar}/transformations/{platform}/artifact")
    Call<TransformationArtifact> getArtifact(@Path("csar") String csar, @Path("platform") String platform);

    @GET("api/csars/{csar}/transformations/{platform}/properties")
    Call<TransformationInputs> getInputs(@Path("csar") String csar, @Path("platform") String platform);

    @POST("api/csars/{csar}/transformations/{platform}/properties")
    Call<ResponseBody> setInputs(@Path("csar") String csar, @Path("platform") String platform, @Body Map<String, String> input);
}
