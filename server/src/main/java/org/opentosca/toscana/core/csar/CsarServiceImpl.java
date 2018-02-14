package org.opentosca.toscana.core.csar;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.plugin.lifecycle.LifecyclePhase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CsarServiceImpl implements CsarService {

    private final CsarDao csarDao;

    @Autowired
    public CsarServiceImpl(CsarDao dao) {
        this.csarDao = dao;
    }

    @Override
    public Csar submitCsar(String identifier, InputStream csarStream) throws CsarIdNotUniqueException {
        if (!csarDao.find(identifier).isPresent()) {
            Csar csar = csarDao.create(identifier, csarStream);
            if (csar.getLifecyclePhase(Csar.Phase.UNZIP).getState() == LifecyclePhase.State.DONE) {
                new CustomTypeInjector(csar.getLog()).inject(csar);
                csar.validate();
            }
            csar.getLog().close();
            return csar;
        } else {
            throw new CsarIdNotUniqueException("Csar id " + identifier + " already in use");
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
