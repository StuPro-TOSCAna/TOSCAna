package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.util.FileSystem;
import org.opentosca.toscana.core.util.PlatformProvider;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.StatusProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO Replace dummies with true implementations

/**
 * Provider Class to supply springs dependency Injection with dummy instances
 */
//@Configuration
public class DummyProvider {
	@Bean
	public Preferences getPreferences() {
		return null;
	}
	@Bean
	public TransformationService getTransformationService() {
		return new DummyTransformationService();
	}

	@Bean
	public CsarService getCsarService() {
		return new DummyCsarService();
	}

	@Bean
	public StatusProvider getSystemStatusProvider() {
		return new DummySystemStatusProvicer();
	}

	@Bean
	public PlatformProvider getPlatformProvider() {
		return new DummyPlatformProvider();
	}

	@Bean
	public FileSystem getFileSystem() {
		return new FileSystemDummy();
	}
}
