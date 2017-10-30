package org.opentosca.toscana.core.dummy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;

public class DummyCsarService implements CsarService {

    private final List<Csar> dummies = new LinkedList<>();

    private final File tmpdir;

    public DummyCsarService(File tmpdir) {
        this.tmpdir = tmpdir;
        String[] names = {"k8s-cluster", "apache-test", "mongo-db"};
        for (String name : names) {
            dummies.add(new DummyCsar(name));
        }
    }

    @Override
    public Csar submitCsar(String name, InputStream csarStream) {
        Csar dummy = new DummyCsar(name);
        if (dummies.contains(dummy)) {
            return null;
        }
        dummies.add(dummy);
        try {
            File dummyPath = new File(tmpdir, name + ".csar");
            FileOutputStream fout = new FileOutputStream(dummyPath);
            writeTo(csarStream, fout);
        } catch (IOException e) {
            return null;
        }
        return dummy;
    }

    private void writeTo(InputStream csarStream, OutputStream fout) throws IOException {
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
    public void deleteCsar(Csar csar) {
        //noop
    }

    @Override
    public List<Csar> getCsars() {
        return dummies;
    }

    @Override
    public Optional<Csar> getCsar(String identifier) {
        Csar csar = null;
        Collection<Csar> csars = getCsars();
        for (Csar c : csars) {
            if (c.getIdentifier().equals(identifier)) {
                csar = c;
                break;
            }
        }
        return Optional.ofNullable(csar);
    }
}
