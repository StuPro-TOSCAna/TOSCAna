package org.opentosca.toscana.core.csar;

import org.apache.commons.io.IOUtils;
import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CsarFilesystemDao implements CsarDao {

    private final static Logger logger = LoggerFactory.getLogger(CsarFilesystemDao.class);

    private Preferences preferences;
    // is automagically set by autowiring preferences setter
    private File dataRoot;

    // a map containing all csars. it is in sync with the status of the file system
    private Map<String, Csar> csarMap = new HashMap<>();

    @Override
    public Csar create(String identifier, InputStream inputStream) {
        String suffix = ".csar";
        File csarArtifact = new File(dataRoot.getPath());
        try {
            Files.copy(
                    inputStream,
                    csarArtifact.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("error while receiving csar input stream", e);
        }

        IOUtils.closeQuietly(inputStream);
        Csar csar = new CsarImpl(identifier);
        return csar;
    }

    @Override
    public boolean delete(Csar csar) {
        // TODO
        throw new UnsupportedOperationException();

    }

    @Override
    public Csar find(String csarName) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Csar> findAll() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Autowired
    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
        dataRoot = preferences.getDataDir();
        dataRoot.mkdir();
    }
}
