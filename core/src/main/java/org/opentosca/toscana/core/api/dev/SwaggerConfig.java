package org.opentosca.toscana.core.api.dev;

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

import java.util.Objects;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Autowired
    public ApiContents apiContents;

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
            s -> Objects.equals(s, "/status"),
            PathSelectors.ant("/platforms/**"),
            PathSelectors.ant("/csars/**")
        );
    }
}
