package org.opentosca.toscana.core.api.upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface TOSCAnaUploadInterface {
    @GET("status")
    Call<ResponseBody> getStatus();

    @Multipart
    @POST("csars/{name}")
    Call<ResponseBody> upload(
        @Part MultipartBody.Part body,
        @Path("name") String name
    );
}
