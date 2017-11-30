package org.opentosca.toscana.retrofit.util;

import org.opentosca.toscana.retrofit.model.ServerError;

public class TOSCAnaServerException extends Exception {
   
    private final ServerError errorResponse;
    private final int statusCode;

    public TOSCAnaServerException(
        String message,
        ServerError errorResponse,
        int statusCode
    ) {
        super(message);
        this.errorResponse = errorResponse;
        this.statusCode = statusCode;
    }

    public ServerError getErrorResponse() {
        return errorResponse;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void print() {
        System.err.printf("%s: %s%n", getMessage(), getStatusCode());
        System.err.printf("%s: %s%n", getErrorResponse().getException(), getErrorResponse().getMessage());
        errorResponse.getLogs().stream().forEach(System.err::println);
    }
}
