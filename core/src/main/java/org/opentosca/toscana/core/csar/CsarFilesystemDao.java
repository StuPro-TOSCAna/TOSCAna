package org.opentosca.toscana.core.csar;

import org.apache.commons.io.FileUtils;
import org.opentosca.toscana.core.util.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Repository
public class CsarFilesystemDao implements CsarDao {

    private final static Logger logger = LoggerFactory.getLogger(CsarFilesystemDao.class.getSimpleName());

    /**
     * the name of the directory which contains the transformations
     */
    public final static String TRANSFORMATION_DIR = "transformations";
    /**
     * the name of the directory which contains the unzipped content of the uploaded CSAR
     */
    public final static String CONTENT_DIR = "content";

    private final File dataDir;

    // a map containing all csars. it should be kept in sync with the status of the file system
    private final Map<String, Csar> csarMap = new HashMap<>();

    @Autowired
    public CsarFilesystemDao(Preferences preferences) {
    	this.dataDir = preferences.getDataDir();
        readFromDisk();
    }

    @Override
    public Csar create(String identifier, InputStream inputStream) {
        csarMap.remove(identifier);
        File appDir = setupDir(identifier);
        File contentDir = new File(appDir, CONTENT_DIR);
        File transformationDir = new File(appDir, TRANSFORMATION_DIR);
        transformationDir.mkdir();
        try {
            UnzipUtility.unzip(new ZipInputStream(inputStream), contentDir.getPath());
            logger.info("extracted csar '{}' into '{}'", identifier, contentDir.getPath());
        } catch (IOException e) {
            logger.error("failed to unzip csar with identifier '{}'", identifier, e);
        }
        // TODO populate csar with files etc..
        // TODO add csar to list of csars
        Csar csar = new CsarImpl(identifier, contentDir);
        csarMap.put(identifier, csar);
        return csar;
    }

    private File setupDir(String identifier) {
        // delete any old entry with same name
        File appFolder = new File(dataDir, identifier);
        try {
            FileUtils.deleteDirectory(appFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        appFolder.mkdir();
        return appFolder;
    }

    @Override
    public void delete(String identifier) {
        File csarDir = new File(dataDir, identifier);
        try {
            FileUtils.deleteDirectory(csarDir);
            logger.info("deleted csar directory '{}'", csarDir);
        } catch (IOException e) {
            logger.error("failed to delete csar directory with identifier '{}'", identifier, e);
        }
    }

    @Override
    public Csar find(String identifier) {
        readFromDisk(); // TODO: change this to some smart behaviour. e.g. file watcher
        return csarMap.get(identifier);

    }

    @Override
    public List<Csar> findAll() {
        readFromDisk(); // TODO: change this to some smart behaviour. e.g. file watcher
        // (refresh on change, not on every access)
        List<Csar> csarList = new ArrayList<>();
        for (Csar csar : csarMap.values()) {
            csarList.add(csar);
        }
        return csarList;
    }

    /**
     * Reads csars from disks
     * post: csarMap reflects contents of DATA_DIR on disk
     */
    private void readFromDisk() {
        csarMap.clear();
        File[] files = dataDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (isCsarDir(file)) {
            	File contentDir = new File(file, CONTENT_DIR);
                Csar csar = new CsarImpl(file.getName(), contentDir);
                csarMap.put(csar.getIdentifier(), csar);
            }
        }
        logger.debug("in-memory csars in synced with file system");

    }

    /**
     * Returns true if given file is a valid csar directory.
     * Valid csar directories must contain 'transformations' and 'content' directory
     */
    private boolean isCsarDir(File file) {
        if (file.isDirectory()) {
            File contentDir = new File(file, CONTENT_DIR);
            File transformationDir = new File(file, TRANSFORMATION_DIR);
            return (contentDir.exists() && transformationDir.exists());
        }
        return false;
    }
}
