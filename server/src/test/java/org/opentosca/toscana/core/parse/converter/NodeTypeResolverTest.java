package org.opentosca.toscana.core.parse.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.converter.util.NodeTypeResolver;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.EntityId;
import org.opentosca.toscana.model.node.RootNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.eclipse.winery.yaml.common.Defaults;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.opentosca.toscana.core.parse.converter.util.NodeTypeResolver.TOSCA_PREFIX;

/**
 Tests whether all natively supported node types of the winery parser are also known to the {@link NodeTypeResolver}.
 */
public class NodeTypeResolverTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(NodeTypeResolverTest.class);

    @Test
    public void supportForAllWineryNodeTypes() {
        Set<String> wineryNodeTypes = getWineryNodeTypes();
        for (String nodeType : wineryNodeTypes) {
            logger.info("Testing conversion of type '{}'", nodeType);
            ServiceGraph graph = new ServiceGraph(log);
            MappingEntity nodeEntity = new MappingEntity(new EntityId(Lists.newArrayList("my", "id")), graph);
            graph.addEntity(nodeEntity);
            ScalarEntity typeEntity = new ScalarEntity(nodeType, nodeEntity.getId().descend("type"), graph);
            graph.addEntity(typeEntity);
            RootNode node = TypeWrapper.wrapNode(nodeEntity);
            assertNotNull(node);
            logger.info("Node Type '{}': known", nodeType);
            System.out.println();
        }
        // successful if no exception thrown
    }

    private Set<String> getWineryNodeTypes() {
        Set<String> knownNodeTypes = new HashSet<>();
        Set<List<String>> typeLists = Sets.newHashSet(
            Defaults.TOSCA_NORMATIVE_NAMES, Defaults.TOSCA_NONNORMATIVE_NAMES);

        for (List<String> typeList : typeLists) {
            knownNodeTypes.addAll(typeList.stream()
                .filter(name -> name.startsWith(TOSCA_PREFIX))
                .collect(Collectors.toSet()));
            for (String name : typeList) {
                Set<String> knownSimpleTypes = new HashSet<>();
                String pattern = TOSCA_PREFIX + ".*\\." + name;
                for (String knownNodeType : knownNodeTypes) {
                    if (knownNodeType.matches(pattern)) {
                        knownSimpleTypes.add(name);
                    }
                }
                knownNodeTypes.addAll(knownSimpleTypes);
            }
        }
        // toscana does not support abstract root node
        knownNodeTypes.remove(TOSCA_PREFIX + "Root");
        return knownNodeTypes;
    }
}
