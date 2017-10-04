package org.opentosca.toscana.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SpringBootApplication
public class Main {

    public static final ApplicationContext appContext;

    static {
        appContext = new ClassPathXmlApplicationContext(
                "/core-beans.xml");
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
