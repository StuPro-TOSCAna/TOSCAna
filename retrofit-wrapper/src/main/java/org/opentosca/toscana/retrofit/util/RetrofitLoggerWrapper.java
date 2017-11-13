package org.opentosca.toscana.retrofit.util;

import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrofitLoggerWrapper implements HttpLoggingInterceptor.Logger {

    private final Logger logger;

    public RetrofitLoggerWrapper(String endpoint) {
        logger = LoggerFactory.getLogger("TOSCAna-API - " + endpoint);
    }

    @Override
    public void log(String message) {
        logger.info(message);
    }
}
