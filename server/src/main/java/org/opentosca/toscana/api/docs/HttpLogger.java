package org.opentosca.toscana.api.docs;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.trace.InMemoryTraceRepository;
import org.springframework.boot.actuate.trace.Trace;
import org.springframework.boot.actuate.trace.TraceRepository;
import org.springframework.stereotype.Component;

/**
 Logs all HTTP requests.
 */
@Component
public class HttpLogger implements TraceRepository {

    private static final Logger logger = LoggerFactory.getLogger(HttpLogger.class);
    private final TraceRepository delegate = new InMemoryTraceRepository();

    @Override
    public List<Trace> findAll() {
        return delegate.findAll();
    }

    @Override
    public void add(Map<String, Object> traceInfo) {
        String method = (String) traceInfo.get("method");
        String path = (String) traceInfo.get("path");
        String timeTaken = (String) traceInfo.get("timeTaken");
        Map<String, Map<String, String>> headerMap = (Map<String, Map<String, String>>) traceInfo.get("headers");
//        Map<String, String> requestMap = headerMap.get("request");
        Map<String, String> responseMap = headerMap.get("response");
        String responseStatus = responseMap.get("status");
        logger.debug("Request: \"{} {}\", response: {} after {}ms", method, path, responseStatus, timeTaken);
        this.delegate.add(traceInfo);
    }
}
