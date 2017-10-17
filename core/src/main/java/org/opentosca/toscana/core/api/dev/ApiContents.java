package org.opentosca.toscana.core.api.dev;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@PropertySource("classpath:api.properties")
//@ConfigurationProperties(prefix = "toscana")
public class ApiContents {
	public String title = "TOSCAna";
	public String version = "1.0.0-SNAPSHOT";
	public String license = "Apache 2.0";
	public String licence_url = "http://www.apache.org/licenses/LICENSE-2.0";
	public String description = "To be Done!";
	public String source_url = "https://github.com/StuPro-TOSCAna/TOSCAna";
}
