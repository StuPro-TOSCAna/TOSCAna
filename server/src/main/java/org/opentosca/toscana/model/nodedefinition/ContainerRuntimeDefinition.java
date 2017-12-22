package org.opentosca.toscana.model.nodedefinition;

public class ContainerRuntimeDefinition extends SoftwareComponentDefinition {

    public static final String HOST_CAPABILITY = "host";
    public static final String SCALABLE_CAPABILITY = "scalable";

    public ContainerRuntimeDefinition() {
        mappings.put(HOST_CAPABILITY, "containerHost");
    }
}
