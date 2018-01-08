package org.opentosca.toscana.api.docs;

import java.io.File;

import org.opentosca.toscana.core.BaseSpringIntegrationTest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 Just for testing now, might be useful once we generate some markdown based documentation.
 */
public class SwaggerAPIDownloadIT extends BaseSpringIntegrationTest {

    private static final String SWAGGER_OUTPUT_DIR = "target/swagger.json";

    @Test
    public void downloadSwaggerAPI() throws Exception {
        String url = getHttpUrl() + "v2/api-docs";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        String result = client.newCall(request).execute().body().string();
        FileUtils.writeByteArrayToFile(new File(SWAGGER_OUTPUT_DIR), result.getBytes());
    }
}
