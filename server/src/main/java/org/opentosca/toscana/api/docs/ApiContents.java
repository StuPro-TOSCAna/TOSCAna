package org.opentosca.toscana.api.docs;

import org.springframework.context.annotation.Configuration;

@Configuration
//@PropertySource("classpath:api.properties")
//@ConfigurationProperties(prefix = "toscana")
public class ApiContents {
    public String title = "TOSCAna";
    public String version = "1.0.0-SNAPSHOT";
    public String license = "Apache 2.0";
    public String licence_url = "http://www.apache.org/licenses/LICENSE-2.0";
    public String description = "To be Done!"; // TODO write application description
    public String source_url = "https://github.com/StuPro-TOSCAna/TOSCAna";
}
