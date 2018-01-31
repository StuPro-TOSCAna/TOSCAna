package org.opentosca.toscana.plugins.cloudfoundry.application;

/*
contains possible CloudFoundry services
 */
public enum ServiceTypes {

    //should be expanded
    MYSQL("mysql");

    private final String name;

    ServiceTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
