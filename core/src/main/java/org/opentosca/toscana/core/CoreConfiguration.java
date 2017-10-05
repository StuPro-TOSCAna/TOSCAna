package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.PreferencesImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {

    @Bean
    public CsarService csarService(){
        CsarServiceImpl bean = new CsarServiceImpl();
        bean.setCsarDao(csarDao());
        return bean;
    }

    @Bean
    public CsarDao csarDao(){
        CsarFilesystemDao bean = new CsarFilesystemDao();
        bean.setPreferences(preferences());
        return bean;
    }

    @Bean
    public Preferences preferences(){
        PreferencesImpl bean = new PreferencesImpl();
        return bean;
    }
}
