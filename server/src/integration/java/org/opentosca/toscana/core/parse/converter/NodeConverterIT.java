package org.opentosca.toscana.core.parse.converter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.opentosca.toscana.core.BaseIntegrationTest;
import org.opentosca.toscana.model.artifact.Repository;

import com.google.common.collect.Sets;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.yaml.common.Defaults;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.opentosca.toscana.core.parse.converter.NodeConverter.TOSCA_PREFIX;

/**
 Tests whether all natively supported node types of the winery parser are also known to the {@link NodeConverter}.
 */
public class NodeConverterIT extends BaseIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(NodeConverterIT.class);

    @Test
    public void supportForAllWineryNodeTypes() throws UnknownNodeTypeException {
        NodeConverter converter = new NodeConverter(new HashSet<Repository>(), logger);
        Set<String> wineryNodeTypes = getWineryNodeTypes();
        for (String nodeType : wineryNodeTypes) {
            logger.info("Testing conversion of type '{}'", nodeType);
            TNodeTemplate template = new TNodeTemplate();
            template.setType(new QName(nodeType));
            try {
                converter.convert("name", template);
                logger.info("Node Type '{}': known", nodeType);
            } catch (UnsupportedOperationException e) {
                logger.info("Node Type '{}': known (not yet supported)", nodeType);
            } catch (NullPointerException e) {
                // ignore
            } finally {
                System.out.println();
            }
        }
        // successful if no UnknownNodeTypeException thrown
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
        knownNodeTypes.remove("tosca.nodes.Root");
        return knownNodeTypes;
    }
}
