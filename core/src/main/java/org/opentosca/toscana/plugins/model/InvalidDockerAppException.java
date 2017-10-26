package org.opentosca.toscana.plugins.model;

public class InvalidDockerAppException extends Exception {

    public InvalidDockerAppException(String s) {
        super(s);
    }

    public InvalidDockerAppException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidDockerAppException(Throwable throwable) {
        super(throwable);
    }

    public InvalidDockerAppException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }

    public InvalidDockerAppException() {
    }
}
