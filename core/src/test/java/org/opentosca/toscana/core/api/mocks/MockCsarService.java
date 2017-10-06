package org.opentosca.toscana.core.api.mocks;

import org.opentosca.toscana.core.api.dummy.DummyCsar;
import org.opentosca.toscana.core.api.utils.StreamUtils;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MockCsarService implements CsarService {
	
	public static String[] names = {"windows-server","apache"};
	
	private List<Csar> csars = new ArrayList<>();

	public MockCsarService() {
		for (int i = 0; i < names.length; i++) {
			csars.add(new DummyCsar(names[i]));
		}
	}

	@Override
	public Csar submitCsar(String name, InputStream csarStream) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			StreamUtils.writeTo(csarStream, out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		DummyCsar c = new DummyCsar(name);
		c.setData(out.toByteArray());
		return c;
	}

	@Override
	public void deleteCsar(Csar csar) {
	}

	@Override
	public List<Csar> getCsars() {
		return csars;
	}

	@Override
	public Csar getCsar(String identifier) {
		return null;
	}
}
