package org.opentosca.toscana.core.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;

import org.springframework.beans.factory.annotation.Autowired;

/**
 Supplies test classes with csars.
 <p>
 Attention: Uses CsarDao internally. If unforeseen errors occurs, check if CsarDao works as advertised
 */
public class TestCsars {

    private final static File CSAR_DIR = new File("src/test/resources/csars");
    private final static File YAML_DIR = new File(CSAR_DIR, "yaml");

    // yaml csars
    // Valid CSARs
    public final static File VALID_MINIMAL_DOCKER = new File(YAML_DIR, "valid/minimal-docker.csar");
    public final static File VALID_MINIMAL_DOCKER_TEMPLATE = new File(YAML_DIR, "valid/minimal-docker/minimal-docker.yaml");
    public final static File VALID_LAMP_NO_INPUT = new File(YAML_DIR, "valid/lamp-noinput.csar");
    public final static File VALID_LAMP_NO_INPUT_TEMPLATE = new File(YAML_DIR, "valid/lamp-noinput/template.yaml");
    public final static File VALID_LAMP_NO_INPUT_MULTI_COMPUTE_TEMPLATE = new File(YAML_DIR, "valid/lamp-multinode/template.yaml");
    public final static File VALID_LAMP_INPUT = new File(YAML_DIR, "valid/lamp-input.csar");
    public final static File VALID_LAMP_INPUT_TEMPLATE = new File(YAML_DIR, "valid/lamp-input/template.yaml");
    public final static File VALID_SINGLE_COMPUTE_WINDOWS_TEMPLATE = new File(YAML_DIR, "valid/single-compute-windows/single-compute-windows.yaml");
    public final static File VALID_SINGLE_COMPUTE_UBUNTU_TEMPLATE = new File(YAML_DIR, "valid/single-compute-ubuntu/single-compute-ubuntu.yaml");
    public final static File VALID_EXPRESS = new File(YAML_DIR, "valid/express.csar");
    public final static File VALID_EXPRESS_TEMPLATE = new File(YAML_DIR, "valid/express/template.yml");
    public final static File VALID_SCALED_DOCKER_TEMPLATE = new File(YAML_DIR, "valid/scale-docker/template.yml");
    public final static File VALID_TASKTRANSLATOR = new File(YAML_DIR, "valid/task-translator.csar");
    public final static File VALID_TASKTRANSLATOR_TEMPLATE = new File(YAML_DIR, "valid/task-translator/template.yaml");
    public final static File VALID_GOPHER = new File(YAML_DIR, "valid/gopher.csar");
    public final static File VALID_GOPHER_TEMPLATE = new File(YAML_DIR, "valid/gopher/template.yml");

    // Invalid CSARs
    public final static File INVALID_DEPENDENCIES_MISSING = new File(YAML_DIR, "invalid/dependencies_missing.csar");
    public final static File INVALID_DOCKERAPP_MISSING = new File(YAML_DIR, "invalid/dockerapp_missing.csar");

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

    public static final class Testing {
        private final static File BASE_DIR = new File(YAML_DIR, "testing_only");
        
        public final static File OUTPUTS = new File(BASE_DIR, "outputs.csar");
        public final static File OUTPUTS_TEMPLATE = new File(BASE_DIR, "outputs/outputs.yaml");
        
        public final static File INPUTS = new File(BASE_DIR, "inputs.csar");
        public final static File INPUTS_TEMPLATE = new File(BASE_DIR, "inputs/inputs.yaml");
        
        public final static File EMPTY_TOPOLOGY = new File(BASE_DIR, "empty-topology.csar");
        public final static File EMPTY_TOPOLOGY_TEMPLATE = new File(BASE_DIR, "empty-topology/template.yaml");
        
        public final static File ENTRYPOINT_MISSING = new File(BASE_DIR, "entrypoint_missing.csar");
        
        public final static File ENTRYPOINT_AMBIGUOUS = new File(BASE_DIR, "entrypoint_ambiguous.csar");
    }
}
