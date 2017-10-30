package org.opentosca.toscana.core.api.dev;

import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource("classpath:api.properties")
//@ConfigurationProperties(prefix = "toscana")
public class ApiContents {
    public final String title = "TOSCAna";
    public final String version = "1.0.0-SNAPSHOT";
    public final String license = "Apache 2.0";
    public final String licence_url = "http://www.apache.org/licenses/LICENSE-2.0";
    public final String description = "To be Done!"; // TODO write application description
    public final String source_url = "https://github.com/StuPro-TOSCAna/TOSCAna";
}
