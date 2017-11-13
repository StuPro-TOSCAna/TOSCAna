package org.opentosca.toscana.retrofit.util;

import org.opentosca.toscana.retrofit.model.ServerError;

public class TOSCAnaServerException extends Exception {
    private ServerError errorResponse;
    private int statusCode;

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
}
