package org.opentosca.toscana.core.parse;

import java.io.File;
import java.util.Objects;

import org.opentosca.toscana.core.transformation.logging.Log;

import org.slf4j.Logger;

public class EntrypointDetector {

    private final Logger logger;
    private final Log log;

    public EntrypointDetector(Log log) {
        this.log = Objects.requireNonNull(log);
        this.logger = log.getLogger(getClass());
    }

    /**
     Note: Entry points are currently only top level .yaml files.
     An entry point specified in the tosca metadata file is currently ignored

     @param csarRoot the file denoting the csar root dir
     @return the entry point yaml file of given csar
     @throws InvalidCsarException if no or more than one top level yaml file was found in given csar
     */
    public File findEntryPoint(File csarRoot) throws InvalidCsarException {
        File[] entryPoints = csarRoot.listFiles((file, s) -> s.matches(".*\\.ya?ml$"));
        if (entryPoints == null) {
            logger.error(String.format("Given directory '%s' does not exist", csarRoot));
            throw new InvalidCsarException(log);
        }
        if (entryPoints.length == 1) {
            File entryPoint = entryPoints[0].getAbsoluteFile();
            logger.debug("Detected entry point of CSAR is '{}'", entryPoint.getName());
            return entryPoint;
        } else if (entryPoints.length > 1) {
            logger.warn("Parsing failed: more than one top level yaml file encountered in given csar");
            throw new InvalidCsarException(log);
        } else {
            logger.error("Parsing failed: no top level yaml file encountered in given csar");
            throw new InvalidCsarException(log);
        }
    }
}
