package org.opentosca.toscana.plugins.cloudformation.visitor;

import java.util.List;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.model.node.Apache;
import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.Database;
import org.opentosca.toscana.model.node.Dbms;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.Nodejs;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import com.google.common.collect.Lists;

/**
 Class for checking the models nodes
 */
public class CheckModelNodeVisitor extends CloudFormationVisitorExtension implements StrictNodeVisitor {

    /**
     Create a <tt>CheckModelNodeVisitor</tt> to check the models nodes.

     @param context TransformationContext to extract topology and logger
     */
    public CheckModelNodeVisitor(TransformationContext context) {
        super(context);
    }

    @Override
    public void visit(Apache node) {
        // noop
    }

    @Override
    public void visit(Compute node) {
        List<OsCapability.Type> supportedTypes = Lists.newArrayList(OsCapability.Type.LINUX);
        //might grow but for now only linux
        List<OsCapability.Distribution> supportedDistributions = Lists.newArrayList(OsCapability.Distribution.UBUNTU);
        //might grow, but for now only ubuntu, maybe already work with others but not yet tested
        OsCapability osCapability = node.getOs();
        //check type
        if (osCapability.getType().isPresent()) {
            OsCapability.Type type = osCapability.getType().get();
            if (!supportedTypes.contains(type)) {
                throw new UnsupportedTypeException("OS Type " + type + " not supported.");
            }
        }
        //check distribution
        if (osCapability.getDistribution().isPresent()) {
            OsCapability.Distribution distribution = osCapability.getDistribution().get();
            if (!supportedDistributions.contains(distribution)) {
                throw new UnsupportedTypeException("OS distribution " + distribution + " not supported.");
            }
        }
    }

    @Override
    public void visit(Database node) {
        // noop
    }

    @Override
    public void visit(MysqlDatabase node) {
        // noop
    }

    @Override
    public void visit(Dbms dbms) {
        // noop
    }

    @Override
    public void visit(MysqlDbms node) {
        // noop
    }

    @Override
    public void visit(WebApplication node) {
        // noop
    }

    @Override
    public void visit(Nodejs nodejs) {
        // noop
    }
}
