package org.opentosca.toscana.core.parse;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class CsarParseServiceImpl implements CsarParseService {

    private Logger logger;
    @Autowired
    CsarDao csarDao;

    @Override
    public TServiceTemplate parse(Csar csar) throws InvalidCsarException {
        logger = csar.getLog().getLogger(getClass());
        Reader reader = new Reader();
        File entrypoint = findEntrypoint(csar);
        TServiceTemplate serviceTemplate = null;
        try {
            serviceTemplate = reader.parse(entrypoint.getParent(), entrypoint.getName());

        } catch (Exception e) {
            // the winery parser is like TNT; we have to catch all exceptions (even better: we run)
            logger.info("An error occured while parsing the csar '{}'", csar, e);
            throw new InvalidCsarException(csar.getLog());
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
        logger = csar.getLog().getLogger(getClass());
        File content = csarDao.getContentDir(csar);
        File[] entrypoints = content.listFiles((file, s) -> s.matches(".*\\.ya?ml$"));
        if (entrypoints.length == 1) {
            File entrypoint = entrypoints[0].getAbsoluteFile();
            logger.info("detected entrypoint of csar '{}' is '{}'", csar.getIdentifier(), entrypoint.getAbsolutePath());
            return entrypoint;
        } else if (entrypoints.length > 1) {
            logger.info("parsing failed: more than one top level yaml file encountered in given csar");
            throw new InvalidCsarException(csar.getLog());
        } else {
            logger.info("parsing failed: no top level yaml file encountered in given csar");
            throw new InvalidCsarException(csar.getLog());
        }
    }
}
