package org.opentosca.toscana.plugins.kubernetes.docker.mapper.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.plugins.kubernetes.docker.mapper.api.model.ImageTags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 This class implements api access to a docker registry using the Registry version 2 API
 <p>
 To connect to the registry retrofit is used and wrapped in this class.
 */
public class DockerRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DockerRegistry.class);

    private RegistryService service;

    private final String baseUrl;

    public DockerRegistry(String baseURL) {
        this.baseUrl = baseURL;
        initRetrofit(baseURL);
    }

    /**
     Helper method to initilialize retrofit
     */
    private void initRetrofit(String url) {
        logger.trace("Initializing Retrofit for Registry {}", url);
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
        service = retrofit.create(RegistryService.class);
    }

    /**
     Returns all tags for a Repository on this registry.
     <p>
     This is a list, because the api splits this into pages
     */
    public List<ImageTags> getTagsForRepository(String user, String repository) throws IOException {
        Object next;
        int page = 1;
        List<ImageTags> imageTags = new ArrayList<>();
        do {
            Response<ImageTags> response = null;
            try {
                response = service.getTagsForRepository(user, repository, page).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Request failed, Invalid response code");
                }
                imageTags.add(response.body());
                next = response.body().getNext();
                page++;
            } catch (Exception e) {
                logger.error("Execution of the request failed");
                logger.error("Processing failed: For {}/{} (Page {}) on '{}'", user, repository, page, baseUrl);
                try {
                    logger.error("The Server responded '{}'", (response != null &&
                        response.errorBody() != null) ? response.errorBody().string() : "");
                } catch (Exception ex) {
                    logger.error("Printing error response failed.", ex);
                    ex.printStackTrace();
                }
                throw e;
            }
        } while (next != null);
        return imageTags;
    }
}
