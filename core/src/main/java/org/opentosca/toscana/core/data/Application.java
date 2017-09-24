package org.opentosca.toscana.core.data;

import java.util.Map;

public class Application {

    /**
     * Stores all scheduled, ongoing or finished transformations of this application.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations;
    private Csar csar;

    public Application(Csar csar){
        this.csar = csar;
    }

    public void transform(Platform targetPlatform){
        // TODO
        throw new UnsupportedOperationException();
    }
}

