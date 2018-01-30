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

    final static File CSAR_DIR = new File("src/test/resources/csars");
    final static File YAML_DIR = new File(CSAR_DIR, "yaml");

    // yaml csars
    public final static File VALID_EMPTY_TOPOLOGY = new File(YAML_DIR, "valid/empty-topology.csar");
    public final static File VALID_EMPTY_TOPOLOGY_TEMPLATE = new File(YAML_DIR, "valid/empty-topology/template.yaml");
    public final static File VALID_MINIMAL_DOCKER = new File(YAML_DIR, "valid/minimal-docker.csar");
    public final static File VALID_MINIMAL_DOCKER_TEMPLATE = new File(YAML_DIR, "valid/minimal-docker/minimal-docker.yaml");
    public final static File VALID_LAMP_NO_INPUT = new File(YAML_DIR, "valid/lamp-noinput.csar");
    public final static File VALID_LAMP_NO_INPUT_TEMPLATE = new File(YAML_DIR, "valid/lamp-noinput/template.yaml");
    public final static File VALID_LAMP_NO_INPUT_MULTI_COMPUTE_TEMPLATE = new File(YAML_DIR, "valid/lamp-multinode/template.yaml");
    public final static File VALID_LAMP_INPUT = new File(YAML_DIR, "valid/lamp-input.csar");
    public final static File VALID_LAMP_INPUT_TEMPLATE = new File(YAML_DIR, "valid/lamp-input/template.yaml");
    public final static File VALID_INPUTS = new File(YAML_DIR, "valid/inputs.csar");
    public final static File VALID_INPUTS_TEMPLATE = new File(YAML_DIR, "valid/inputs/inputs.yaml");
    public final static File VALID_OUTPUTS = new File(YAML_DIR, "valid/outputs.csar");
    public final static File VALID_OUTPUTS_TEMPLATE = new File(YAML_DIR, "valid/outputs/outputs.yaml");
    public static final File VALID_SINGLE_COMPUTE_WINDOWS_TEMPLATE = new File(YAML_DIR, "valid/single-compute-windows/single-compute-windows.yaml");
    public static final File VALID_SINGLE_COMPUTE_UBUNTU_TEMPLATE = new File(YAML_DIR, "valid/single-compute-ubuntu/single-compute-ubuntu.yaml");
    public final static File INVALID_DEPENDENCIES_MISSING = new File(YAML_DIR, "invalid/dependencies_missing.csar");
    public final static File INVALID_DOCKERAPP_MISSING = new File(YAML_DIR, "invalid/dockerapp_missing.csar");
    public final static File INVALID_ENTRYPOINT_MISSING = new File(YAML_DIR, "invalid/entrypoint_missing.csar");
    public final static File INVALID_ENTRYPOINT_AMBIGUOUS = new File(YAML_DIR, "invalid/entrypoint_ambiguous.csar");

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
