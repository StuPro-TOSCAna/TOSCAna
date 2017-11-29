package org.opentosca.toscana.plugins.kubernetes.docker.mapper.util;

import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperTest;

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
@Profile("base-image-mapper")
@Configuration
public class MapperTestConfiguration {
    @Bean
    public BaseImageMapper mapper() throws Exception {
        return MapperTest.init();
    }
}
