package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.model.EffectiveModelFactory;

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
            throw e;
        }
    }

    // this call is expensive, use with care
    private void validate(Csar csar) throws InvalidCsarException {
        // TODO integrate winery parser as validator
        // test whether EffectiveModel can get created without throwing an error
        try {
            // test if conversion does evoke errors
            effectiveModelFactory.create(csar);
            // TODO improve error handling
        } catch (Exception e) {
            throw new InvalidCsarException(csar.getLog());
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
