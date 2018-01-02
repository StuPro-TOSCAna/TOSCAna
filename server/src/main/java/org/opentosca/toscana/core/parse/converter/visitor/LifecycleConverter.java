package org.opentosca.toscana.core.parse.converter.visitor;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.operation.ConfigureLifecycle;
import org.opentosca.toscana.model.operation.ConfigureLifecycle.ConfigureLifecycleBuilder;
import org.opentosca.toscana.model.operation.Operation;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.operation.StandardLifecycle.StandardLifecycleBuilder;

import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;

public class LifecycleConverter {

    private final Set<Artifact> artifacts;

    public LifecycleConverter(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public StandardLifecycle convertStandard(TInterfaceDefinition interfaceDefinition) {
        Map<String, TOperationDefinition> operationDefinitions = interfaceDefinition.getOperations();
        return ((StandardLifecycleBuilder) StandardLifecycle.builder()
            .create(getOperation(operationDefinitions.get("create")))
            .configure(getOperation(operationDefinitions.get("configure")))
            .start(getOperation(operationDefinitions.get("start")))
            .stop(getOperation(operationDefinitions.get("stop")))
            .delete(getOperation(operationDefinitions.get("delete")))
            .inputs(getInOrOutputs(interfaceDefinition.getInputs())))
            .build();
    }

    public ConfigureLifecycle convertConfigure(TInterfaceDefinition interfaceDefinition) {
        Map<String, TOperationDefinition> operationDefinitions = interfaceDefinition.getOperations();
        return ((ConfigureLifecycleBuilder) ConfigureLifecycle.builder()
            .preConfigureSource(getOperation(operationDefinitions.get("pre_configure_source")))
            .preConfigureTarget(getOperation(operationDefinitions.get("pre_configure_target")))
            .postConfigureSource(getOperation(operationDefinitions.get("post_configure_source")))
            .postConfigureTarget(getOperation(operationDefinitions.get("post_configure_target")))
            .addTarget(getOperation(operationDefinitions.get("add_target")))
            .addSource(getOperation(operationDefinitions.get("add_source")))
            .targetChanged(getOperation(operationDefinitions.get("target_changed")))
            .removeTarget(getOperation(operationDefinitions.get("remove_target")))
            .inputs(getInOrOutputs(interfaceDefinition.getInputs())))
            .build();
    }

    private Operation getOperation(TOperationDefinition operationDefintion) {
        Operation operation = null;
        if (operationDefintion != null) {
            operation = Operation.builder()
                .description(operationDefintion.getDescription())
                .artifact(getArtifact(operationDefintion.getImplementation()))
                .dependencies(operationDefintion.getImplementation().getDependencies().stream()
                    .map(QName::getLocalPart)
                    .collect(Collectors.toSet()))
                .inputs(getInOrOutputs(operationDefintion.getInputs()))
                .outputs(getInOrOutputs(operationDefintion.getOutputs()))
                .build();
        }
        return operation;
    }

    private Artifact getArtifact(TImplementation implementation) {
        Artifact artifact = null;
        if (implementation != null) {
            Optional<Artifact> optionalArtifact = artifacts.stream()
                .filter(a -> a.getName().equals(implementation.getPrimary().getLocalPart()))
                .findFirst();
            artifact = optionalArtifact.orElse(Artifact
                .builder("", implementation.getPrimary().getLocalPart())
                .build());
        }
        return artifact;
    }

    private Set<OperationVariable> getInOrOutputs(Map<String, TPropertyAssignmentOrDefinition> inputDefinitions) {
        return inputDefinitions.entrySet().stream()
            .map(entry -> new OperationVariable(entry.getKey(), (String) ((TPropertyAssignment) entry.getValue()).getValue()))
            .collect(Collectors.toSet());
    }
}
