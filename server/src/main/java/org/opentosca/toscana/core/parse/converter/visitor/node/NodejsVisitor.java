package org.opentosca.toscana.core.parse.converter.visitor.node;

import org.opentosca.toscana.core.parse.converter.visitor.Context;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.Nodejs.NodejsBuilder;

import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;

public class NodejsVisitor<NodeT extends Nodejs, BuilderT extends NodejsBuilder> extends WebServerVisitor<NodeT, BuilderT> {

    private final static String GITHUB_URL_PROPERTY = "github_url";

    @Override
    protected void handleProperty(TPropertyAssignment node, Context<BuilderT> parameter, BuilderT builder, Object value) {
        switch (parameter.getKey()) {
            case GITHUB_URL_PROPERTY:
                builder.githubUrl((String) value);
                break;
            default:
                super.handleProperty(node, parameter, builder, value);
                break;
        }
    }
}
