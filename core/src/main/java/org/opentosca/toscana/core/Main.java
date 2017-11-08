package org.opentosca.toscana.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@ComponentScan("org.opentosca.toscana")
@SpringBootApplication
@EnableHypermediaSupport(type = HAL)
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
