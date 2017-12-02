package org.opentosca.toscana.core.parse.converter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.BlockStorage;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.ContainerApplication;
import org.opentosca.toscana.model.node.ContainerRuntime;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.DockerApplication;
import org.opentosca.toscana.model.node.LoadBalancer;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.ObjectStorage;
import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.node.SoftwareComponent;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.node.WebServer;
import org.opentosca.toscana.model.node.WordPress;

import com.google.common.collect.Sets;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;

public class NodeConverter {

    public static final String TOSCA_PREFIX = "tosca.nodes.";

    private Map<String, BiFunction<String, TNodeTemplate, RootNode>> conversionMap = new HashMap<>();

    NodeConverter() {
        addRule("Compute", this::toCompute);
        addRule("Container.Application", this::toContainerApplication);
        addRule("Container.Runtime", this::toContainerRuntime);
        addRule("BlockStorage", "Storage", this::toBlockStorage);
        addRule("Database", this::toDatabase);
        addRule("Database.MySQL", this::toMysqlDbms);
        addRule("Application.Docker", "Container", this::toDockerApplication);
        addRule("DBMS", this::toDbms);
        addRule("DBMS.MySQL", this::toMysqlDbms);
        addRule("LoadBalancer", this::toLoadBalancer);
        addRule("ObjectStorage", "Storage", this::toObjectStorage);
        addRule("SoftwareComponent", this::toSoftwareComponent);
        addRule("WebApplication", this::toWebApplication);
        addRule("WordPress", "WebApplication", this::toWordPress);
        addRule("WebServer", this::toWebServer);
        addRule("Apache", "WebServer", this::toApache);
        addRule("Nodejs", "WebServer", this::toNodejs);
    }

    private void addRule(String simpleType, String typePrefix, BiFunction<String, TNodeTemplate, RootNode> conversion) {
        Set<String> typeSet = getTypes(simpleType, typePrefix);
        typeSet.stream().forEach(typeName -> conversionMap.put(typeName, conversion));
    }
    
    private void addRule(String simpleType, BiFunction<String, TNodeTemplate, RootNode> conversion) {
        addRule(simpleType, null, conversion);
    }

    private Set<String> getTypes(String simpleName, String prefix) {
        String typePrefix = (prefix == null) ? "" : prefix + ".";
        return Sets.newHashSet(simpleName, TOSCA_PREFIX + typePrefix + simpleName);
    }

    RootNode convert(String name, TNodeTemplate template) throws UnknownNodeTypeException {
        String nodeType = template.getType().getLocalPart();
        BiFunction<String, TNodeTemplate, RootNode> conversion = conversionMap.get(nodeType);
        if (conversion == null) {
            throw new UnknownNodeTypeException(String.format(
                "Node type '%s' is not supported by the internal model", template.getType()));
        }
        return conversion.apply(name, template);
    }

    private Apache toApache(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private BlockStorage toBlockStorage(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private Compute toCompute(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private ContainerApplication toContainerApplication(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private ContainerRuntime toContainerRuntime(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private Database toDatabase(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private Dbms toDbms(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private DockerApplication toDockerApplication(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private LoadBalancer toLoadBalancer(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private MysqlDbms toMysqlDbms(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private Nodejs toNodejs(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private ObjectStorage toObjectStorage(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private SoftwareComponent toSoftwareComponent(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private WebApplication toWebApplication(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private WebServer toWebServer(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }

    private WordPress toWordPress(String name, TNodeTemplate template) {
        throw new UnsupportedOperationException();
    }
}
