package org.opentosca.toscana.retrofit.util;

import okhttp3.logging.HttpLoggingInterceptor;

public enum LoggingMode {
    OFF(HttpLoggingInterceptor.Level.NONE),
    LOW(HttpLoggingInterceptor.Level.BASIC),
    MEDIUM(HttpLoggingInterceptor.Level.HEADERS),
    HIGH(HttpLoggingInterceptor.Level.BODY);
    
    private HttpLoggingInterceptor.Level level;

    LoggingMode(HttpLoggingInterceptor.Level level) {
        this.level = level;
    }

    public HttpLoggingInterceptor.Level getLevel() {
        return level;
    }
}
