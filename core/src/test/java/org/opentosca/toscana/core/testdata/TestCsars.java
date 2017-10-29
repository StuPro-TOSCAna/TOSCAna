package org.opentosca.toscana.core.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 Supplies test classes with csars Attention: Uses CsarDao internally. If unforeseen errrors occour, check if CsarDao
 works as advertised
 */
public class TestCsars {

    private final static Logger logger = LoggerFactory.getLogger(TestCsars.class.getName());

    private final static File CSAR_DIR = new File("src/test/resources/csars");
    private final static File YAML_DIR = new File(CSAR_DIR, "yaml");

    // yaml csars
    public final static File CSAR_YAML_VALID_DOCKER_SIMPLETASK = new File(YAML_DIR, "valid/simple-task.csar");
    public static final File CSAR_YAML_INVALID_DEPENDENCIES_MISSING = new File(YAML_DIR, "invalid/dependencies_missing.csar");
    public static final File CSAR_YAML_INVALID_DOCKERAPP_MISSING = new File(YAML_DIR, "invalid/dockerapp_missing.csar");
    public static final File CSAR_YAML_INVALID_ENTRYPOINT_MISSING = new File(YAML_DIR, "invalid/entrypoint_missing.csar");
    public static final File CSAR_YAML_INVALID_ENTRYPOINT_AMBIGUOUS = new File(YAML_DIR, "invalid/entrypoint_ambiguous.csar");

    @Autowired
    private CsarDao csarDao;

    /**
     Creates given file as csar. Caution: Uses CsarDao internally

     @param file a csar
     @return instance of csar
     */
    public Csar getCsar(File file) throws FileNotFoundException {
        String identifier = file.getName().toLowerCase().replaceAll("[^a-z0-1_-]", "");
        return getCsar(identifier, file);
    }

    /**
     Creates given file as csar. Caution: Uses CsarDao internally

     @param identifier identifier for new csar
     @param file       a csar
     @return instance of csar
     */
    public Csar getCsar(String identifier, File file) throws FileNotFoundException {
        Csar csar = csarDao.create(identifier, new FileInputStream(file));
        return csar;
    }
}
