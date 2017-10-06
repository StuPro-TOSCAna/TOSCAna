package org.opentosca.toscana.core.api.mocks;

import org.opentosca.toscana.core.csar.CsarService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockCsarServiceConfiguration {
	@Bean
	public CsarService dummy() {
		return new MockCsarService();
	}
}
