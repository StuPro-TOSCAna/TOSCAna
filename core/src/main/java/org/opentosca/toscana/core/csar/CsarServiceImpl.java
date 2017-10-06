package org.opentosca.toscana.core.csar;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collection;

//@Service
public class CsarServiceImpl implements CsarService {

    private CsarDao csarDao;

    @Override
    public Csar uploadCsar(String name, InputStream csarStream) {

        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteCsar(Csar csar) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Csar> getCsars() {
        // TODO
        throw new UnsupportedOperationException();
    }

	@Override
	public Csar getCsar(String identifier) {
    	// TODO
		throw new UnsupportedOperationException();
	}

	@Autowired
    public void setCsarDao(CsarDao csarDao){
        this.csarDao = csarDao;
    }
}
