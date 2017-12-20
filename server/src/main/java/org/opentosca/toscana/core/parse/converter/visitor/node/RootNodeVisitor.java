package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.RequirementConversion;
import org.opentosca.toscana.core.parse.converter.RequirementConverter;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.core.parse.converter.visitor.LifecycleConverter;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.capability.Capability;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.operation.StandardLifecycle;
import org.opentosca.toscana.model.relation.DependsOn;
import org.opentosca.toscana.model.relation.RootRelationship;
import org.opentosca.toscana.model.requirement.Requirement;

import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;

public abstract class RootNodeVisitor<NodeT extends RootNode, BuilderT extends RootNode.RootNodeBuilder> extends DescribableEntityVisitor<NodeT, BuilderT> {

    @Override
    public ConversionResult<NodeT> visit(TNodeTemplate node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        builder.nodeName(parameter.getNodeName());
        return super.visit(node, parameter);
    }

    @Override
    public ConversionResult<NodeT> visit(TArtifactDefinition node, Context<BuilderT> parameter) {
        Repository repository = null;
        if (node.getRepository() != null) {
            repository = parameter.getRepositories().stream()
                .filter(repo -> node.getRepository().equals(repo.getName()))
                .findFirst()
                .orElse(null);
        }
        String file = (node.getFiles().size() > 0) ? node.getFiles().get(0) : null;
        Artifact artifact = Artifact
            .builder(parameter.getKey(), file)
            .description(node.getDescription())
            .deployPath(node.getDeployPath())
            .repository(repository)
            .build();
        parameter.getArtifacts().add(artifact);
        return null;
    }

    @Override
    public ConversionResult<NodeT> visit(TInterfaceDefinition node, Context<BuilderT> parameter) {
        switch (parameter.getKey()) {
            case "Standard":
                StandardLifecycle lifecycle = new LifecycleConverter(parameter.getArtifacts()).convertStandard(node);
                parameter.getBuilder().standardLifecycle(lifecycle);
                break;
            default:
                // TODO handle custom interfaces
                throw new UnsupportedOperationException();
        }
        return super.visit(node, parameter);
    }

    @Override
    public ConversionResult<NodeT> visit(TRequirementAssignment node, Context<BuilderT> parameter) {
        BuilderT builder = parameter.getBuilder();
        handleRequirement(node, parameter, builder);
        return null;
    }

    protected void handleRequirement(TRequirementAssignment requirement, Context<BuilderT> context, BuilderT builder) {
        // handling of generic requirements
        builder.requirement(provideRequirement(requirement, context, DependsOn.class));
    }

    protected <CapabilityT extends Capability, RequirementNodeT extends RootNode, RelationshipT extends RootRelationship>
    Requirement<CapabilityT, RequirementNodeT, RelationshipT> provideRequirement(TRequirementAssignment node, Context parameter, Class<RelationshipT> relationshipClass) {
        RequirementConversion conversion = new RequirementConverter().<CapabilityT, RequirementNodeT, RelationshipT>convert(node, relationshipClass);
        parameter.addRequirementConversion(conversion);
        return conversion.requirement;
    }
}
