package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.*;
import org.opentosca.toscana.core.util.Preferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public CsarService csarService(){
        CsarServiceImpl bean = new CsarServiceImpl();
        return bean;
    }

    @Bean
    public CsarDao csarDao(){
        CsarFilesystemDao bean = new CsarFilesystemDao(preferences());
        return bean;
    }

    @Bean
    public Preferences preferences(){
        return new PreferencesMock();
    }
}
