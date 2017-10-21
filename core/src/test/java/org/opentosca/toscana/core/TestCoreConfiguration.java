package org.opentosca.toscana.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.yml")
public class TestCoreConfiguration extends CoreConfiguration {

    @Bean
    public TestData testData() {
        TestData bean = new TestData(csarDao());
        return bean;
    }
}
