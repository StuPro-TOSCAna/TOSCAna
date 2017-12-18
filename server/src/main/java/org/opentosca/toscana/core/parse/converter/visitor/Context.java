package org.opentosca.toscana.core.parse.converter.visitor;

import java.util.HashSet;
import java.util.Set;

import org.opentosca.toscana.core.parse.converter.RequirementConversion;
import org.opentosca.toscana.model.AbstractEntity.AbstractEntityBuilder;
import org.opentosca.toscana.model.artifact.Artifact;
import org.opentosca.toscana.model.artifact.Repository;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;

public class Context<BuilderT extends AbstractEntityBuilder> extends AbstractParameter<Context<BuilderT>> {

    private final String nodeName;
    private final BuilderT builder;
    private final Set<Repository> repositories;
    private final Set<Artifact> artifacts = new HashSet<>();
    private final Set<RequirementConversion> requirementConversions = new HashSet<>();

    public Context(String name, BuilderT builder, Set<Repository> repositories) {
        this.nodeName = name;
        this.builder = builder;
        this.repositories = repositories;
    }

    public Context(BuilderT builder) {
        this(null, builder, null);
    }

    @Override
    public Context copy() {
        return this;
    }

    @Override
    public Context self() {
        return this;
    }

    public String getNodeName() {
        return nodeName;
    }

    public BuilderT getBuilder() {
        return builder;
    }

    public Set<Repository> getRepositories() {
        return repositories;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public Set<RequirementConversion> getRequirementConversions() {
        return requirementConversions;
    }

    public void addRequirementConversion(RequirementConversion conversion) {
        requirementConversions.add(conversion);
    }
}
