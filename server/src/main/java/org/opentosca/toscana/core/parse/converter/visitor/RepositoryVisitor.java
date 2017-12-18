package org.opentosca.toscana.core.parse.converter.visitor;

import java.net.MalformedURLException;
import java.net.URL;

import org.opentosca.toscana.model.artifact.Repository;

import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.slf4j.Logger;

public class RepositoryVisitor extends AbstractVisitor<SetResult<Repository>, Parameter> {

    private final Logger logger;

    public RepositoryVisitor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public SetResult<Repository> visit(TRepositoryDefinition node, Parameter parameter) {
        String name = parameter.getKey();
        try {
            URL url = new URL(node.getUrl());
            Repository repository = Repository.builder(name, url)
                // TODO build propper credential conversion
//                .credential()
                .description(node.getDescription())
                .build();
            return new SetResult<>(repository);
        } catch (MalformedURLException e) {
            logger.error("Encountered malformed URL in repository definition '{}'", name, e);
            // TODO throw something meaningful
            throw new IllegalStateException();
        }
    }
}
