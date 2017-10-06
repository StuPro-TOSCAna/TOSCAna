package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.util.Preferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.yml")
public class CoreConfiguration {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
    @Bean
    public CsarService csarService(){
        CsarServiceImpl bean = new CsarServiceImpl();
        bean.setCsarDao(csarDao());
        return bean;
    }

    @Bean
    public CsarDao csarDao(){
        CsarFilesystemDao bean = new CsarFilesystemDao(preferences());
        return bean;
    }

    @Bean
    public Preferences preferences(){
        Preferences bean = new Preferences();
        return bean;
    }
}
