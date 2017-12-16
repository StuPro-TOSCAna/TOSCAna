package org.opentosca.toscana.plugins.cloudfoundry.application;

/*
contains possible CloudFoundry services
 */
public enum CloudFoundryServiceType {

    //will be expanded
    MYSQL("mysql");

    private final String name;

    CloudFoundryServiceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
