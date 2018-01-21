package org.opentosca.toscana.core.parse;

import java.io.File;

public class TestTemplates {

    private static final File BASE_PATH = new File("src/test/resources/converter");
    private static final File FUNCTIONS = new File(BASE_PATH, "functions");
    private static final File NORMALIZATION = new File(BASE_PATH, "normalization");
    private static final File SYMBOLIC_LINKS = new File(BASE_PATH, "symbolic_links");
    private static final File TOSCA_ELEMENTS = new File(BASE_PATH, "tosca_elements");
    private static final File NODES = new File(TOSCA_ELEMENTS, "nodes");

    public static class Normalization {
        public static final File REPOSITORY = new File(NORMALIZATION, "repository_norm.yaml");
        public static final File OPERATION = new File(NORMALIZATION, "operation_norm.yaml");
    }

    public static class SymbolicLinks {
        public static final File REQUIREMENT = new File(SYMBOLIC_LINKS, "requirement_link.yaml");
        public static final File REPOSITORY = new File(SYMBOLIC_LINKS, "repository_link.yaml");
        public static final File ARTIFACT = new File(SYMBOLIC_LINKS, "implementation_link.yaml");
    }

    public static class Functions {
        public static final File GET_INPUT = new File(FUNCTIONS, "get_input.yaml");
        public static final File GET_PROPERTY = new File(FUNCTIONS, "get_property.yaml");
        public static final File GET_PROPERTY_SELF = new File(FUNCTIONS, "get_property_self.yaml");
        public static final File GET_PROPERTY_IN_INTERFACE = new File(FUNCTIONS, "get_property_in-interface.yaml");
    }

    public static class Nodes {
        public static final File SOFTWARE_COMPONENT = new File(NODES, "software-component.yaml");
    }

    public static class ToscaElements {
        public static final File REPOSITORY = new File(TOSCA_ELEMENTS, "repository.yaml");
        public static final File CREDENTIAL = new File(TOSCA_ELEMENTS, "credential.yaml");
        public static final File INPUT = new File(TOSCA_ELEMENTS, "input.yaml");
        public static final File INPUT_NO_VALUE = new File(TOSCA_ELEMENTS, "input_no-value.yaml");
        public static final File OUTPUT = new File(TOSCA_ELEMENTS, "output.yaml");
        public static final File INTERFACE = new File(TOSCA_ELEMENTS, "interface.yaml");
        public static final File CAPABILITY = new File(TOSCA_ELEMENTS, "capability.yaml");
        public static final File REQUIREMENT = new File(TOSCA_ELEMENTS, "requirement.yaml");
        public static final File NODE = new File(TOSCA_ELEMENTS, "node.yaml");
    }
}

