package org.opentosca.toscana.core.csar;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

public class CsarServiceImpl implements CsarService {

    private CsarDao csarDao;

    @Override
    public Csar submitCsar(String identifier, InputStream csarStream) {
    	Csar csar = csarDao.create(identifier, csarStream);	
    	// TODO parse
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
	public Csar getCsar(String identifier) {
    	Csar csar = csarDao.find(identifier);
    	return csar;
	}

	@Autowired
    public void setCsarDao(CsarDao csarDao){
        this.csarDao = csarDao;
    }
}
