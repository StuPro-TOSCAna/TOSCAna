package org.opentosca.toscana.core;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Supplies test classes with csars
 * Attention: Uses CsarDao internally. If unforeseen errrors occour, check if CsarDao works as advertised
 */
public class TestData {

    public CsarDao csarDao;

    @Autowired
    public TestData(CsarDao csarDao) {
        this.csarDao = csarDao;
    }

    private final static Logger logger = LoggerFactory.getLogger(TestData.class.getName());

    private final static File CSAR_DIR = new File("src/test/resources/csars");
    private final static File XML_DIR = new File(CSAR_DIR, "xml");
    private final static File YAML_DIR = new File(CSAR_DIR, "yaml");


    // yaml csars
    public final static File CSAR_YAML_VALID_SIMPLETASK = new File(YAML_DIR, "valid/simple-task.csar");

    // xml csars
    public final static File CSAR_XML_VALID_MOODLE = new File(XML_DIR, "valid/Moodle.csar");


    /**
     * @param file a csar
     * @return instance of csar
     * @throws FileNotFoundException
     */
    public Csar getCsar(File file) throws FileNotFoundException {
        String identifier = file.getName().toLowerCase().replaceAll("[^a-z0-1_-]", "");
        Csar csar = csarDao.create(identifier, new FileInputStream(file));
        return csar;

    }


}
