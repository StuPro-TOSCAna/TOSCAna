package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.parse.EntrypointDetector;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.util.LifecyclePhase;
import org.opentosca.toscana.model.EffectiveModelFactory;

import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarServiceImpl implements CsarService {

    private final static Logger logger = LoggerFactory.getLogger(CsarService.class.getName());

    private final CsarDao csarDao;
    private final EffectiveModelFactory effectiveModelFactory;

    @Autowired
    public CsarServiceImpl(CsarDao dao, EffectiveModelFactory effectiveModelFactory) {
        this.csarDao = dao;
        this.effectiveModelFactory = effectiveModelFactory;
    }

    @Override
    public Csar submitCsar(String identifier, InputStream csarStream) throws InvalidCsarException {
        Csar csar = csarDao.create(identifier, csarStream);
        try {
            validate(csar);
            return csar;
        } catch (InvalidCsarException e) {
            logger.warn("Failed to submit csar", e);
            // cleanup
            csarDao.delete(csar.getIdentifier());
            csar.getLog().close();
            throw e;
        }
    }

    // this call is expensive, use with care
    private void validate(Csar csar) throws InvalidCsarException {
        Log log = csar.getLog();
        Logger logger = log.getLogger(getClass());
        LifecyclePhase validatePhase = csar.getLifecyclePhase(Csar.Phase.VALIDATE);
        validatePhase.setState(LifecyclePhase.State.EXECUTING);
        logger.info("Validating csar '{}'", csar.getIdentifier());
        logger.debug("  > Validating TOSCA template", csar.getIdentifier());
        File entryPoint = new EntrypointDetector(log).findEntryPoint(csar.getContentDir());
        try {
            Reader.getReader().parse(Paths.get(csar.getContentDir().toString()), Paths.get(entryPoint.toString()));
            logger.info("Template validation successful");
        } catch (MultiException e) {
            logger.error("Template validation failed", e);
            throw new InvalidCsarException(log);
        }

        try {
            // test if conversion does evoke errors
            logger.info("  > Constructing model from TOSCA template");
            effectiveModelFactory.create(csar);
            validatePhase.setState(LifecyclePhase.State.DONE);
            logger.info("Model construction successful");
        } catch (Exception e) {
            validatePhase.setState(LifecyclePhase.State.FAILED);
            logger.error("Model construction failed", csar.getIdentifier(), e);
            throw new InvalidCsarException(log);
        }
    }

    @Override
    public void deleteCsar(Csar csar) {
        csarDao.delete(csar.getIdentifier());
    }

    @Override
    public List<Csar> getCsars() {
        return csarDao.findAll();
    }

    @Override
    public Optional<Csar> getCsar(String identifier) {
        return csarDao.find(identifier);
    }
}
