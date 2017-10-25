package org.opentosca.toscana.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.yml")
public class CoreConfiguration {

//    @Bean
//    public CsarService csarService() {
//        CsarServiceImpl bean = new CsarServiceImpl(csarDao(), csarParser());
//        return bean;
//    }
//
//    @Bean
//    public CsarDao csarDao() {
//        CsarFilesystemDao bean = new CsarFilesystemDao(preferences());
//        return bean;
//    }
//    
//    @Bean
//    public TransformationDao transformationDao(){
//        TransformationFilesystemDao bean = new TransformationFilesystemDao();
//        return bean;
//    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
