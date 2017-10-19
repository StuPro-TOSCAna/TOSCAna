package org.opentosca.toscana.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Main {

    public final static ApplicationContext appContext;
    private final static Logger logger = LoggerFactory.getLogger(Main.class.getName());

    static {
        appContext = new AnnotationConfigApplicationContext(CoreConfiguration.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
