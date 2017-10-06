package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DummyCsarService implements CsarService {

	private String[] names = {"k8s-cluster", "apache-test", "mongo-db"};
	Set<Csar> dummies = new HashSet<>();

	public DummyCsarService() {
		for (String name : names) {
			dummies.add(new DummyCsar(name));
		}
	}

	@Override
	public Csar uploadCsar(String name, InputStream csarStream) {
		Csar dummy = new DummyCsar(name);
		if (dummies.contains(dummy)) {
			return null;
		}
		dummies.add(dummy);
		try {
			File dummyPath = new File(name + ".csar");
			FileOutputStream fout = new FileOutputStream(dummyPath);
			writeTo(csarStream, fout);
		} catch (IOException e) {
			return null;
		}
		return dummy;
	}

	public static void writeTo(InputStream csarStream, OutputStream fout) throws IOException {
		byte[] data = new byte[512];
		int bytesRead = 0;
		while (bytesRead != -1) {
            bytesRead = csarStream.read(data);
            if (bytesRead != -1) {
                fout.write(data, 0, bytesRead);
            }
        }
		csarStream.close();
		fout.close();
	}

	@Override
	public boolean deleteCsar(Csar csar) {
		return false;
	}

	@Override
	public Collection<Csar> getCsars() {

		return dummies;
	}
}
