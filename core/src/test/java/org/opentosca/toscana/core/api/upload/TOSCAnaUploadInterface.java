package org.opentosca.toscana.core.api.upload;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface TOSCAnaUploadInterface {
    @GET("status")
    Call<ResponseBody> getStatus();

    @Multipart
    @POST("csars/{checkStateNoPropsSet}")
    Call<ResponseBody> upload(
        @Part MultipartBody.Part body,
        @Path("checkStateNoPropsSet") String name
    );
}
