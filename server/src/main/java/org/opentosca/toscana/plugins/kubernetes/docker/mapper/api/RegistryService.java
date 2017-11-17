package org.opentosca.toscana.plugins.kubernetes.docker.mapper.api;

import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model.ImageTags;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RegistryService {

    @GET("/v2/repositories/{username}/{repository}/tags")
    Call<ImageTags> getTagsForRepository(
        @Path("username") String username,
        @Path("repository") String repository,
        @Query("page") int page
    );
}
