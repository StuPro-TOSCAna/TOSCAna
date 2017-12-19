package org.opentosca.toscana.core.parse;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.parse.converter.ModelConverter;
import org.opentosca.toscana.model.EffectiveModel;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarParseServiceImpl implements CsarParseService {

    private Logger logger = LoggerFactory.getLogger(CsarParseService.class);

    @Autowired
    private CsarDao csarDao;

    @Override
    public EffectiveModel parse(Csar csar) throws InvalidCsarException {
        logger = csar.getLog().getLogger(getClass());
        File entryPoint = findEntryPoint(csar);
        try {
            return parse(entryPoint);
        } catch (Exception e) {
            logger.warn("An error occurred while parsing CSAR '{}'", csar, e);
            throw new InvalidCsarException(csar.getLog());
        }
    }

    @Override
    public EffectiveModel parse(File template) throws Exception {
        Reader reader = Reader.getReader();
        TServiceTemplate serviceTemplate;
        Path parent = template.getParentFile().toPath();
        Path file = Paths.get(template.getName());
        serviceTemplate = reader.parse(parent, file);
        ModelConverter converter = new ModelConverter(logger);

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
            logger.info("detected entry point of csar '{}' is '{}'", csar.getIdentifier(), entryPoint.getName());
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
