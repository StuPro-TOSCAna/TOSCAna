package org.opentosca.toscana.plugins.cloudformation.mapper;

import org.opentosca.toscana.model.node.custom.JavaRuntime;

import org.slf4j.Logger;

public class JavaRuntimeMapper {
    private final Logger logger;
    private static final String DEFAULT_STACK_CONFIG = "64bit Amazon Linux 2017.09 v2.6.6 running Java ";
    private static final String JAVA_8 = "8";

    public JavaRuntimeMapper(Logger logger) {
        this.logger = logger;
    }

    public String mapRuntimeToStackConfig(JavaRuntime javaRuntime) {
        logger.debug("Map JavaRuntime {}", javaRuntime.getEntityName());
        String javaVersion;
        if (javaRuntime.getComponentVersion().isPresent()) {
            javaVersion = javaRuntime.getComponentVersion().get().replaceAll("1.", "");
        } else {
            javaVersion = JAVA_8;
        }
        logger.debug("JavaVersion: {}", javaVersion);
        return DEFAULT_STACK_CONFIG + javaVersion;
    }
}
