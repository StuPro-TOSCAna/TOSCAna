package org.opentosca.toscana.core.csar;


import java.io.InputStream;
import java.util.Collection;

class CsarServiceImpl implements CsarService {

    @Override
    public Csar uploadCsar(String name, InputStream csarStream) {
        return null;
    }

    @Override
    public boolean deleteCsar(Csar csar) {
        return false;
    }

    @Override
    public Collection<Csar> getCsars() {
        return null;
    }
}
