package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import org.opentosca.toscana.core.Profiles;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("!" + Profiles.EXCLUDE_BASE_IMAGE_MAPPER)
public class BaseImageMapperConfiguration {

    @Bean
    public DockerBaseImages[] getDockerBaseImages() {
        return DockerBaseImages.values();
    }
}
