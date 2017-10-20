package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarFilesystemDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.csar.CsarServiceImpl;
import org.opentosca.toscana.core.parse.CsarParser;
import org.opentosca.toscana.core.parse.CsarParserImpl;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.Preferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.yml")
public class TestCoreConfiguration extends CoreConfiguration {

	@Bean
	public TestData testData() {
		TestData bean = new TestData(csarDao());
		return bean;
	}
}
