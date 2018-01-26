package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.model.EffectiveModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarServiceImpl implements CsarService {

    private final static Logger logger = LoggerFactory.getLogger(CsarService.class.getName());

    private final CsarDao csarDao;

    @Autowired
    public CsarServiceImpl(CsarDao dao) {
        this.csarDao = dao;
    }

    @Override
    public Csar submitCsar(String identifier, InputStream csarStream) throws InvalidCsarException {
        Csar csar = csarDao.create(identifier, csarStream);
        try {
            populateWithTemplate(csar);
            return csar;
        } catch (InvalidCsarException e) {
            logger.warn("Failed to submit csar", e);
            // cleanup
            csarDao.delete(csar.getIdentifier());
            throw e;
        }
    }

    private void populateWithTemplate(Csar csar) throws InvalidCsarException {
        if (!csar.getModel().isPresent()) {
            EffectiveModel model = new EffectiveModel(csar, csarDao.getContentDir(csar));
            csar.setModel(model);
        }
    }

    @Override
    public void deleteCsar(Csar csar) {
        csarDao.delete(csar.getIdentifier());
    }

    @Override
    public List<Csar> getCsars() {
        List<Csar> list = csarDao.findAll();
        for (Csar csar : list) {
            try {
                populateWithTemplate(csar);
            } catch (InvalidCsarException e) {
                logger.error("Encountered invalid csar in repository.", csar, e);
                // TODO wait for discussion outcome, maybe cleanup disk
            }
        }
        return list;
    }

    @Override
    public Optional<Csar> getCsar(String identifier) {
        Optional<Csar> csar = csarDao.find(identifier);
        if (csar.isPresent()) {
            try {
                populateWithTemplate(csar.get());
            } catch (InvalidCsarException e) {
                logger.error("Encountered invalid csar in repository.", csar, e);
                // TODO wait for discussion outcome, maybe cleanup disk
            }
        }
        return csar;
    }
}
