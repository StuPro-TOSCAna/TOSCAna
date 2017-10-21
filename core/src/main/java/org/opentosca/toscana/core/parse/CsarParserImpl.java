package org.opentosca.toscana.core.parse;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.opentosca.toscana.core.csar.Csar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsarParserImpl implements CsarParser {

    private final static Logger logger = LoggerFactory.getLogger(CsarParser.class);

    @Override
    public TServiceTemplate parse(Csar csar) throws InvalidCsarException {
        Reader reader = new Reader();
        File entrypoint = findEntrypoint(csar);
        TServiceTemplate serviceTemplate = null;
        try {
            serviceTemplate = reader.parse(entrypoint.getParent(), entrypoint.getName());

        } catch (MultiException e) {
            logger.error("An error occured while parsing the csar '{}'", csar, e);
        }
        return serviceTemplate;
    }

    /**
     * Note: Entrypoints are currently only top level yaml files.
     * An entrypoint specified in the tosca metadata file is currently ignored
     *
     * @param csar a csar object
     * @return the entrypoint yaml file of given csar
     * @throws InvalidCsarException if no or more than one top level yaml file was found in given csar
     */
    private File findEntrypoint(Csar csar) throws InvalidCsarException {
        File content = csar.getRoot();
        File[] entrypoints = content.listFiles((file, s) -> s.matches(".*\\.ya?ml$"));
        if (entrypoints.length == 1) {
            File entrypoint = entrypoints[0].getAbsoluteFile();
            logger.debug("detected entrypoint of csar '{}' is '{}'", csar.getIdentifier(), entrypoint.getAbsolutePath());
            return entrypoint;
        } else if (entrypoints.length > 1) {
            throw new InvalidCsarException("more than one top level yaml file encountered in given csar");
        } else {
            throw new InvalidCsarException("no top level yaml file encountered in given csar");
        }
    }
}
