package org.opentosca.toscana.core.parse.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.core.parse.converter.visitor.RepositoryVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.SetResult;
import org.opentosca.toscana.core.transformation.properties.Property;
import org.opentosca.toscana.model.EffectiveModel;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.RootNode;

import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.yaml.common.validator.support.Parameter;
import org.slf4j.Logger;

/**
 Contains logic to convertStandard a {@link TServiceTemplate} to a {@link EffectiveModel}
 */
public class ModelConverter {

    private final Logger logger;

    public ModelConverter(Logger logger) {
        this.logger = logger;
    }

    public EffectiveModel convert(TServiceTemplate serviceTemplate) throws UnknownNodeTypeException {
        logger.debug("Convert service template to normative model");
        Set<Repository> repositories = getRepositories(serviceTemplate);

        Set<Property> inputs = new InputConverter().convert(serviceTemplate);
        Set<ConversionResult<RootNode>> result = convertNodeTemplates(serviceTemplate.getTopologyTemplate(), repositories);
        Set<RootNode> nodes = fulfillRequirements(result);
        return new EffectiveModel(nodes, inputs);
    }

    private Set<Repository> getRepositories(TServiceTemplate serviceTemplate) {
        RepositoryVisitor visitor = new RepositoryVisitor(logger);
        SetResult<Repository> repositoryResult = visitor.visit(serviceTemplate, new Parameter());
        Set<Repository> repositories = (repositoryResult == null) ? new HashSet<Repository>() : repositoryResult.get();
        return repositories;
    }

    private Set<ConversionResult<RootNode>> convertNodeTemplates(TTopologyTemplateDefinition topology, Set<Repository> repositories) throws UnknownNodeTypeException {
        Map<String, TNodeTemplate> templateMap;
        if (topology != null) {
            templateMap = topology.getNodeTemplates();
        } else {
            logger.warn("Topology template of service template does not contain any node templates");
            templateMap = new HashMap<>();
        }

        Set<ConversionResult<RootNode>> results = new HashSet<>();
        NodeConverter nodeConverter = new NodeConverter(repositories, logger);
        for (Map.Entry<String, TNodeTemplate> entry : templateMap.entrySet()) {
            ConversionResult<RootNode> conversionResult = nodeConverter.convert(entry.getKey(), entry.getValue());
            results.add(conversionResult);
        }
        return results;
    }

    private Set<RootNode> fulfillRequirements(Set<ConversionResult<RootNode>> results) {
        for (ConversionResult<RootNode> result : results) {
            for (RequirementConversion requirementConversion : result.getRequirementConversions()) {
                for (ConversionResult<RootNode> potentialFulfiller : results) {
                    String nodeName = potentialFulfiller.getResult().getNodeName();
                    if (requirementConversion.fulfiller.equals(nodeName)) {
                        requirementConversion.requirement.getFulfillers().add(potentialFulfiller.getResult());
                    }
                }
            }
        }
        return results.stream().map(result -> result.getResult()).collect(Collectors.toSet());
    }
}
