package org.opentosca.toscana.plugins.dummy;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.properties.Property;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyConfiguration {

    @Bean
    public TransformationPlugin dummyPluginProps() {
        String platformId = "dummy-p";
        String platformName = "Dummy Platform (Properties)";
        Set<Property> platformProperties = new HashSet<>();
        return new DummyPlugin(new Platform(platformId, platformName, platformProperties), false);
    }
    
    @Bean
    public TransformationPlugin dummyPluginNoPropsFail() {
        String platformId = "dummy-npf";
        String platformName = "Dummy Platform (No Properties, Fail during exec)";
        Set<Property> platformProperties = new HashSet<>();
        return new DummyPlugin(new Platform(platformId, platformName, platformProperties), true);
    }
    
    @Bean
    public TransformationPlugin dummyPluginNoProps() {
        String platformId = "dummy-np";
        String platformName = "Dummy Platform (No Properties)";
        Set<Property> platformProperties = new HashSet<>();
        return new DummyPlugin(new Platform(platformId, platformName, platformProperties), false);
    }
}
