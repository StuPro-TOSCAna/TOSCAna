package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.model.EffectiveModelFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarServiceImpl implements CsarService {

    private final CsarDao csarDao;
    private final EffectiveModelFactory effectiveModelFactory;

    @Autowired
    public CsarServiceImpl(CsarDao dao, EffectiveModelFactory effectiveModelFactory) {
        this.csarDao = dao;
        this.effectiveModelFactory = effectiveModelFactory;
    }

    @Override
    public Csar submitCsar(String identifier, InputStream csarStream) {
        Csar csar = csarDao.create(identifier, csarStream);
        csar.validate();
        csar.getLog().close();
        return csar;
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
