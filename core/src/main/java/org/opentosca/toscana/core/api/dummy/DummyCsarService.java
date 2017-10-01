package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;

import java.io.InputStream;
import java.util.Collection;

public class DummyCsarService implements CsarService{
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
