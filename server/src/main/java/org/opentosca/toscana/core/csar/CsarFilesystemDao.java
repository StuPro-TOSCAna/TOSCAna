package org.opentosca.toscana.core.csar;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipInputStream;

import javax.annotation.PostConstruct;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;
import org.opentosca.toscana.core.util.Preferences;
import org.opentosca.toscana.core.util.ZipUtility;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class CsarFilesystemDao implements CsarDao {

    /**
     The name of the directory which contains all csars
     */
    public final static String CSARS_DIR = "csars";
    /**
     the name of the directory which contains the transformations
     */
    public final static String TRANSFORMATION_DIR = "transformations";

    private final static Logger logger = LoggerFactory.getLogger(CsarFilesystemDao.class);

    private final TransformationDao transformationDao;
    private final File dataDir;

    // a map containing all csars. it should be kept in sync with the status of the file system
    private final Map<String, Csar> csarMap = new HashMap<>();

    @Autowired
    public CsarFilesystemDao(Preferences preferences, @Lazy TransformationDao transformationDao) {
        this.dataDir = new File(preferences.getDataDir(), CSARS_DIR);
        this.transformationDao = transformationDao;

        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                logger.error("Failed to create data dir '{}'", dataDir.getAbsolutePath());
            }
        }
    }

    @PostConstruct
    public void init() {
        transformationDao.setCsarDao(this);
        readFromDisk();
    }

    @Override
    public Csar create(String identifier, InputStream inputStream) {
        csarMap.remove(identifier);
        File csarDir = setupDir(identifier);
        File contentDir = new File(csarDir, CsarImpl.CONTENT_DIR);
        File transformationDir = new File(csarDir, TRANSFORMATION_DIR);
        transformationDir.mkdir();
        try {
            ZipUtility.unzip(new ZipInputStream(inputStream), contentDir.getPath());
            logger.info("Extracted csar '{}' into '{}'", identifier, contentDir.getPath());
        } catch (IOException e) {
            logger.error("Failed to unzip csar with identifier '{}'", identifier, e);
        }
        Csar csar = new CsarImpl(getRootDir(identifier), identifier, getLog(identifier));
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
            csarMap.remove(identifier);
            logger.info("Deleted csar directory '{}'", csarDir);
        } catch (IOException e) {
            logger.error("Failed to delete csar directory with identifier '{}'", identifier, e);
        }
    }

    @Override
    public Optional<Csar> find(String identifier) {
        Csar csar = csarMap.get(identifier);
        return Optional.ofNullable(csar);
    }

    @Override
    public List<Csar> findAll() {
        List<Csar> csarList = new ArrayList<>();
        csarList.addAll(csarMap.values());
        return csarList;
    }

    @Override
    public File getRootDir(Csar csar) {
        return getRootDir(csar.getIdentifier());
    }

    private File getRootDir(String csarIdentifier) {
        return new File(dataDir, csarIdentifier);
    }

    @Override
    public File getContentDir(Csar csar) {
        return new File(getRootDir(csar), CsarImpl.CONTENT_DIR);
    }

    @Override
    public File getTransformationsDir(Csar csar) {
        return new File(getRootDir(csar), TRANSFORMATION_DIR);
    }

    /**
     Reads csars from disks post: csarMap reflects contents of DATA_DIR on disk
     */
    private void readFromDisk() {
        csarMap.clear();
        dataDir.mkdir();
        File[] files = dataDir.listFiles();
        for (File file : files) {
            if (isCsarDir(file)) {
                String id = file.getName();
                CsarImpl csar = new CsarImpl(getRootDir(id), id, getLog(id));
                csarMap.put(csar.getIdentifier(), csar);
                List<Transformation> transformations = transformationDao.find(csar);
                csar.setTransformations(transformations);
            }
        }
        logger.debug("Read csars from disk");
    }

    /**
     Returns true if given file is a valid csar directory.
     Valid csar directories must contain 'transformations' and 'content' directory
     */
    private boolean isCsarDir(File file) {
        if (file.isDirectory()) {
            File contentDir = new File(file, CsarImpl.CONTENT_DIR);
            File transformationDir = new File(file, TRANSFORMATION_DIR);
            return (contentDir.exists() && transformationDir.exists());
        }
        return false;
    }

    private Log getLog(String identifier) {
        File logFile = new File(getRootDir(identifier), identifier + ".log");
        return new LogImpl(logFile);
    }
}
