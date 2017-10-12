package org.opentosca.toscana.core;

import org.opentosca.toscana.core.api.dummy.DummyPlatformProvider;
import org.opentosca.toscana.core.api.dummy.DummySystemStatusProvicer;
import org.opentosca.toscana.core.api.dummy.FileSystemDummy;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.parse.CsarParser;
import org.opentosca.toscana.core.parse.CsarParserImpl;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.StatusProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.yml")
public class CoreConfiguration {


//    @Bean
//    public CsarService csarService(){
//        CsarServiceImpl bean = new CsarServiceImpl(csarDao(), csarParser());
//        return bean;
//    }
//    @Bean
//    public CsarDao csarDao(){
//        CsarFilesystemDao bean = new CsarFilesystemDao(preferences());
//        return bean;
//    }

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	//TODO Replace with implementation of the Status Provider
	@Bean
	public StatusProvider statusProvider() {
		return new DummySystemStatusProvicer();
	}

	//TODO Replace with filesystem implementation
	@Bean
	public FileSystem fileSystem() {
		return new FileSystemDummy();
	}

	@Bean
	public PlatformProvider platformProvider() {
		return new DummyPlatformProvider();
	}

	@Bean
	public Preferences preferences() {
		Preferences bean = new Preferences();
		return bean;
	}

	@Bean
	public CsarParser csarParser() {
		CsarParserImpl bean = new CsarParserImpl();
		return bean;
	}
}
