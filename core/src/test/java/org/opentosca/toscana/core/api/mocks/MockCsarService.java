package org.opentosca.toscana.core.api.mocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opentosca.toscana.core.api.utils.StreamUtils;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.dummy.DummyCsar;

public class MockCsarService implements CsarService {

    public static String[] names = {"windows-server", "apache"};

    private List<Csar> csars = new ArrayList<>();

    public MockCsarService() {
        for (String name : names) {
            csars.add(new DummyCsar(name));
        }
    }

    @Override
    public Csar submitCsar(String name, InputStream csarStream) {
        if (getCsar(name) != null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            StreamUtils.writeTo(csarStream, out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        DummyCsar c = new DummyCsar(name);
        c.setData(out.toByteArray());
        csars.add(c);
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
        for (Csar csar : csars) {
            if (csar.getIdentifier().equals(identifier)) {
                return csar;
            }
        }
        return null;
    }
}
