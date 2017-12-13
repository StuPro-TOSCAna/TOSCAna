package org.opentosca.toscana.plugins.kubernetes.docker.mapper.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.DockerBaseImages;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.TagStorage;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 Import this annotation if you need the base image mapper in your integration tests
 it will load a file from the test resources in order to perform the mappings.
 To keep loading times at a minimum
 add it using the following annotation on the class
 <code>@ContextConfiguration(classes = {MapperTestConfiguration.class})</code>
 and
 <code>@ActiveProfiles({"base-image-mapper"})</code>
 */
@SuppressWarnings("Duplicates")
@Profile("base-image-mapper")
@Configuration
public class MapperTestConfiguration {

    @Bean
    public TagStorage tagStorage(Preferences preferences) throws Exception {
        new File(preferences.getDataDir(), "misc").mkdirs();
        File file = new File(preferences.getDataDir(), TagStorage.DOCKER_IMAGE_TAGS);
        InputStream input = getClass().getResourceAsStream("/kubernetes/base-image-mapper/docker-tagbase.json");
        FileOutputStream out = new FileOutputStream(file);

        IOUtils.copy(input, out);
        input.close();
        out.close();

        return new TagStorage(preferences);
    }

    @Bean
    public BaseImageMapper mapper(TagStorage storage) {
        return new BaseImageMapper(DockerBaseImages.values(), storage);
    }
}
