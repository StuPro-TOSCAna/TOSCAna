package org.opentosca.toscana.core.parse.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.opentosca.toscana.core.parse.converter.util.BuilderUtil;
import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.core.parse.converter.visitor.ConversionResult;
import org.opentosca.toscana.core.parse.converter.visitor.node.ApacheVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.BlockStorageVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.ComputeVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.ContainerApplicationVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.ContainerRuntimeVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.DatabaseVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.DbmsVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.DescribableEntityVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.DockerApplicationVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.LoadBalancerVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.MysqlDatabaseVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.MysqlDbmsVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.NodejsVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.ObjectStorageVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.SoftwareComponentVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.WebApplicationVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.WebServerVisitor;
import org.opentosca.toscana.core.parse.converter.visitor.node.WordPressVisitor;
import org.opentosca.toscana.model.DescribableEntity;
import org.opentosca.toscana.model.DescribableEntity.DescribableEntityBuilder;
import org.opentosca.toscana.model.artifact.Repository;
import org.opentosca.toscana.model.node.Apache.ApacheBuilder;
import org.opentosca.toscana.model.node.BlockStorage.BlockStorageBuilder;
import org.opentosca.toscana.model.node.Compute.ComputeBuilder;
import org.opentosca.toscana.model.node.ContainerApplication.ContainerApplicationBuilder;
import org.opentosca.toscana.model.node.ContainerRuntime.ContainerRuntimeBuilder;
import org.opentosca.toscana.model.node.Database.DatabaseBuilder;
import org.opentosca.toscana.model.node.Dbms.DbmsBuilder;
import org.opentosca.toscana.model.node.DockerApplication.DockerApplicationBuilder;
import org.opentosca.toscana.model.node.LoadBalancer.LoadBalancerBuilder;
import org.opentosca.toscana.model.node.MysqlDatabase.MysqlDatabaseBuilder;
import org.opentosca.toscana.model.node.MysqlDbms.MysqlDbmsBuilder;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.ObjectStorage.ObjectStorageBuilder;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.SoftwareComponent.SoftwareComponentBuilder;
import org.opentosca.toscana.model.node.WebApplication.WebApplicationBuilder;
import org.opentosca.toscana.model.node.WebServer.WebServerBuilder;
import org.opentosca.toscana.model.node.WordPress.WordPressBuilder;

import com.google.common.collect.Sets;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.slf4j.Logger;

/**
 Contains logic to convert TOSCA node templates into nodes of the EffectiveModel
 */
class NodeConverter {

    static final String TOSCA_PREFIX = "tosca.nodes.";
    private final Set<Repository> repositories;
    private final Logger logger;

    private Map<String, ConversionType> conversionMap = new HashMap<>();

    public NodeConverter(Set<Repository> repositories, Logger logger) {
        this.repositories = repositories;
        this.logger = logger;
        addRule("Compute", new ConversionType(ComputeBuilder.class, new ComputeVisitor()));
        addRule("Container.Application", new ConversionType(ContainerApplicationBuilder.class, new ContainerApplicationVisitor()));
        addRule("Container.Runtime", new ConversionType(ContainerRuntimeBuilder.class, new ContainerRuntimeVisitor()));
        addRule("Storage", "BlockStorage", new ConversionType(BlockStorageBuilder.class, new BlockStorageVisitor()));
        addRule("Database", new ConversionType(DatabaseBuilder.class, new DatabaseVisitor()));
        addRule("Database.MySQL", new ConversionType(MysqlDatabaseBuilder.class, new MysqlDatabaseVisitor()));
        addRule("Container", "Application.Docker", new ConversionType(DockerApplicationBuilder.class, new DockerApplicationVisitor()));
        addRule("DBMS", new ConversionType(DbmsBuilder.class, new DbmsVisitor()));
        addRule("DBMS.MySQL", new ConversionType(MysqlDbmsBuilder.class, new MysqlDbmsVisitor()));
        addRule("LoadBalancer", new ConversionType(LoadBalancerBuilder.class, new LoadBalancerVisitor()));
        addRule("Storage", "ObjectStorage", new ConversionType(ObjectStorageBuilder.class, new ObjectStorageVisitor()));
        addRule("SoftwareComponent", new ConversionType(SoftwareComponentBuilder.class, new SoftwareComponentVisitor()));
        addRule("WebApplication", new ConversionType(WebApplicationBuilder.class, new WebApplicationVisitor()));
        addRule("WebApplication", "WordPress", new ConversionType(WordPressBuilder.class, new WordPressVisitor()));
        addRule("WebServer", new ConversionType(WebServerBuilder.class, new WebServerVisitor()));
        addRule("WebServer", "Apache", new ConversionType(ApacheBuilder.class, new ApacheVisitor()));
        addRule("WebServer", "Nodejs", new ConversionType(Nodejs.class, new NodejsVisitor()));
    }

    /**
     Establishes a correlation between a node type (string) and its construction method.

     @param simpleType the shorthand type of a node (e.g.: "Compute")
     @param type       the type of builder class and visitor class which is applied for given simpletype
     */
    private void addRule(String simpleType, ConversionType type) {
        addRule(null, simpleType, type);
    }

    /**
     Overloading {@link #addRule}

     @param simpleType {@link #addRule}
     @param typePrefix the prefix needed to construct the full type name: {@code tosca.nodes.<typePrefix>.<simpleType>}
     @param type       {@link #addRule}
     */
    private void addRule(String typePrefix, String simpleType, ConversionType type) {
        Set<String> typeSet = getTypes(simpleType, typePrefix);
        typeSet.forEach(typeName -> conversionMap.put(typeName, type));
    }

    private Set<String> getTypes(String simpleName, String prefix) {
        String typePrefix = (prefix == null) ? "" : prefix + ".";
        return Sets.newHashSet(simpleName, TOSCA_PREFIX + typePrefix + simpleName);
    }

    public ConversionResult<RootNode> convert(String name, TNodeTemplate template) throws UnknownNodeTypeException {
        String nodeType = template.getType().getLocalPart();
        logger.debug("> Convert node template '{}' (type: '{}')", name, nodeType);
        ConversionType type = conversionMap.get(nodeType);
        if (type == null) {
            throw new UnknownNodeTypeException(String.format(
                "Node type '%s' is not supported by the internal model", template.getType()));
        }
        try {
            return toNode(name, template, type.builderClass, type.visitor);
        } catch (UnsupportedOperationException e) {
            throw new UnsupportedOperationException(
                String.format("Converting node templates of type '%s' not yet supported",
                    template.getType()), e);
        }
    }

    private <NodeT extends DescribableEntity, BuilderT extends DescribableEntityBuilder,
        VisitorT extends DescribableEntityVisitor<NodeT, BuilderT>>
    ConversionResult<NodeT> toNode(String name, TNodeTemplate template, Class<BuilderT> builderType, VisitorT visitor) {
        Context<BuilderT> context = new Context<>(name, BuilderUtil.newInstance(builderType), repositories);
        return visitor.visit(template, context);
    }

    private static class ConversionType<VisitorT extends DescribableEntityVisitor> {
        public final Class<? extends DescribableEntityBuilder> builderClass;
        public final VisitorT visitor;

        private ConversionType(Class<? extends DescribableEntityBuilder> builderClass, VisitorT visitor) {
            this.builderClass = builderClass;
            this.visitor = visitor;
        }
    }
}
