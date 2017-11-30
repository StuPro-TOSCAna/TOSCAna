package org.opentosca.toscana.core.parse;

import java.io.File;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.model.EffectiveModel;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarParseServiceImpl implements CsarParseService {

    private Logger logger;

    @Autowired
    private CsarDao csarDao;

    @Override
    public EffectiveModel parse(Csar csar) throws InvalidCsarException {
        logger = csar.getLog().getLogger(getClass());
        Reader reader = new Reader();
        File entryPoint = findEntryPoint(csar);
        TServiceTemplate serviceTemplate;
        try {
            serviceTemplate = reader.parse(entryPoint.getParent(), entryPoint.getName());
        } catch (Exception e) {
            logger.warn("An error occured while parsing csar '{}'", csar, e);
            throw new InvalidCsarException(csar.getLog());
        }
        ModelConverter converter = new ModelConverter();
        return converter.convert(serviceTemplate);
    }

    /**
     Note: Entry points are currently only top level .yaml files.
     An entry point specified in the tosca metadata file is currently ignored

     @param csar a csar object
     @return the entry point yaml file of given csar
     @throws InvalidCsarException if no or more than one top level yaml file was found in given csar
     */
    private File findEntryPoint(Csar csar) throws InvalidCsarException {
        File content = csarDao.getContentDir(csar);
        File[] entryPoints = content.listFiles((file, s) -> s.matches(".*\\.ya?ml$"));
        if (entryPoints.length == 1) {
            File entryPoint = entryPoints[0].getAbsoluteFile();
            logger.warn("detected entry point of csar '{}' is '{}'", csar.getIdentifier(), entryPoint.getAbsolutePath());
            return entryPoint;
        } else if (entryPoints.length > 1) {
            logger.warn("parsing failed: more than one top level yaml file encountered in given csar");
            throw new InvalidCsarException(csar.getLog());
        } else {
            logger.error("parsing failed: no top level yaml file encountered in given csar");
            throw new InvalidCsarException(csar.getLog());
        }
    }
}
