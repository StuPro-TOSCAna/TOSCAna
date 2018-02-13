package org.opentosca.toscana.core.parse.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.converter.util.TypeResolver;
import org.opentosca.toscana.core.parse.converter.util.ToscaStructure;
import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.core.parse.model.ScalarEntity;
import org.opentosca.toscana.core.parse.model.ServiceGraph;
import org.opentosca.toscana.model.BaseToscaElement;

import com.google.common.collect.Sets;
import org.eclipse.winery.yaml.common.Defaults;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;
import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.CAPABILITY;
import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.NODE;
import static org.opentosca.toscana.core.parse.converter.util.TypeResolver.ElementType.RELATIONSHIP;

/**
 Tests whether all natively supported node types of the winery parser are also known to the {@link TypeResolver}.
 */
public class TypeResolverTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(TypeResolverTest.class);

    @Test
    public void supportForAllWineryNodeTypes() {
        Set<String> wineryNodeTypes = getWineryNodeTypes();
        for (String nodeType : wineryNodeTypes) {
            logger.info("Testing conversion of type '{}'", nodeType);
            ServiceGraph graph = new ServiceGraph(logMock());
            MappingEntity nodeEntity = new MappingEntity(ToscaStructure.NODE_TEMPLATES.descend("my-node"), graph);
            graph.addEntity(nodeEntity);
            ScalarEntity typeEntity = new ScalarEntity(nodeType, nodeEntity.getId().descend("type"), graph);
            graph.addEntity(typeEntity);
            BaseToscaElement node = TypeWrapper.wrapTypedElement(nodeEntity);
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

        String prefixPattern = String.format("^(%s|%s|%s)", NODE.prefix, RELATIONSHIP.prefix, CAPABILITY.prefix);
        for (List<String> typeList : typeLists) {
            knownNodeTypes.addAll(typeList.stream()
                .filter(name -> name.matches(prefixPattern + ".*"))
                .collect(Collectors.toSet()));
            Set<String> knownSimpleTypes = new HashSet<>();
            for (String name : typeList) {
                String pattern = prefixPattern + ".*\\." + name;
                for (String knownNodeType : knownNodeTypes) {
                    if (knownNodeType.matches(pattern)) {
                        knownSimpleTypes.add(name);
                        break;
                    }
                }
            }
            knownNodeTypes.addAll(knownSimpleTypes);
        }
        // toscana does not support abstract types
        knownNodeTypes.remove(NODE.prefix + "Root");
        knownNodeTypes.remove(CAPABILITY.prefix + "Root");
        knownNodeTypes.remove(RELATIONSHIP.prefix + "Root");
        return knownNodeTypes;
    }
}
