package org.opentosca.toscana.api.docs;

import java.util.Objects;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SuppressWarnings({"unchecked", "Guava"})
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final ApiContents apiContents;

    @Autowired
    public SwaggerConfig(ApiContents apiContents) {
        this.apiContents = apiContents;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(getPaths())
            .build();
    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(apiContents.title)
            .license(apiContents.license)
            .licenseUrl(apiContents.licence_url)
            .version(apiContents.version)
            .description(apiContents.description)
            .contact(new Contact("", apiContents.source_url, ""))
            .build();
    }

    private Predicate<String> getPaths() {
        return Predicates.or(
            s -> Objects.equals(s, "/api/status"),
            PathSelectors.ant("/api/platforms/**"),
            PathSelectors.ant("/api/csars/**"),
            PathSelectors.ant("/api/*")
        );
    }
}
