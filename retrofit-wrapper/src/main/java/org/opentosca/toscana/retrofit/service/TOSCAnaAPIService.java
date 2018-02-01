package org.opentosca.toscana.retrofit.service;

import java.util.Map;

import org.opentosca.toscana.retrofit.model.Csar;
import org.opentosca.toscana.retrofit.model.Platform;
import org.opentosca.toscana.retrofit.model.Transformation;
import org.opentosca.toscana.retrofit.model.TransformationLogs;
import org.opentosca.toscana.retrofit.model.TransformationProperties;
import org.opentosca.toscana.retrofit.model.TransformerStatus;
import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.model.embedded.PlatformResources;
import org.opentosca.toscana.retrofit.model.embedded.TransformationResources;
import org.opentosca.toscana.retrofit.model.hal.HALResource;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TOSCAnaAPIService {
    @DELETE("/api/csars/{csarName}/delete")
    Call<ResponseBody> deleteCsar(
        @Path("csarName") String name
    );

    @DELETE("/api/csars/{csarName}/transformations/{platform}/delete")
    Call<ResponseBody> deleteTransformation(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @GET("/api/")
    Call<HALResource> getAPIRoot();

    @GET("/api/status/health")
    Call<TransformerStatus> getTransformerHealth();

    @GET("/api/status/metrics")
    Call<Map<String, Object>> getTransformerMetrics();

    @GET("/api/platforms")
    Call<PlatformResources> getPlatforms();

    @GET("/api/platforms/{platform}")
    Call<Platform> getPlatformDetails(@Path("platform") String name);

    @GET("/api/csars")
    Call<CsarResources> getCsars();

    @GET("/api/csars/{csarName}")
    Call<Csar> getCsarDetails(@Path("csarName") String name);

    @GET("/api/csars/{csarName}/transformations/")
    Call<TransformationResources> getTransformationsForCsar(
        @Path("csarName") String csarName
    );

    @GET("/api/csars/{csarName}/transformations/{platform}")
    Call<Transformation> getTransformationDetails(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @GET("/api/csars/{csarName}/transformations/{platform}/logs")
    Call<TransformationLogs> getTransformationLogs(
        @Path("csarName") String csarName,
        @Path("platform") String platform,
        @Query("start") Long start
    );

    @POST("api/csars/{csarName}/transformations/{platform}/create")
    Call<ResponseBody> createTransformation(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @POST("/api/csars/{csarName}/transformations/{platform}/start")
    Call<ResponseBody> startTransformation(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @GET("/api/csars/{csarName}/transformations/{platform}/artifact")
    Call<ResponseBody> getArtifact(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @GET("/api/csars/{csarName}/transformations/{platform}/properties")
    Call<TransformationProperties> getProperties(
        @Path("csarName") String csarName,
        @Path("platform") String platform
    );

    @PUT("/api/csars/{csarName}/transformations/{platform}/properties")
    Call<ResponseBody> updateProperties(
        @Path("csarName") String csarName,
        @Path("platform") String platform,
        @Body TransformationProperties body
    );

    @Multipart
    @POST("/api/csars/{name}")
    Call<ResponseBody> upload(
        @Part MultipartBody.Part body,
        @Path("name") String name
    );
}
