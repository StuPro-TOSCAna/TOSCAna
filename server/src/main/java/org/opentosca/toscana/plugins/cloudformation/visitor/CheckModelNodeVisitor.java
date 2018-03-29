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
import org.opentosca.toscana.model.node.custom.JavaApplication;
import org.opentosca.toscana.model.node.custom.JavaRuntime;
import org.opentosca.toscana.model.visitor.StrictNodeVisitor;
import org.opentosca.toscana.model.visitor.UnsupportedTypeException;

import com.google.common.collect.Lists;

/**
 Checks the model's nodes whether they are supported or not.
 <br>
 The visitor implements the {@link StrictNodeVisitor} interface which means every method that is not overridden
 will throw an {@link UnsupportedTypeException}. These types are not supported.
 */
public class CheckModelNodeVisitor extends CloudFormationVisitor implements StrictNodeVisitor {

    /**
     Creates a <tt>CheckModelNodeVisitor</tt> to check the models nodes.

     @param context {@link TransformationContext} to extract the topology and a logger
     */
    public CheckModelNodeVisitor(TransformationContext context) {
        super(context);
    }

    /**
     {@link Apache} node is supported.
     */
    @Override
    public void visit(Apache node) {
        // noop
    }

    /**
     {@link Compute} node is supported.
     <br>
     Only Linux, Ubuntu is supported!
     */
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

    /**
     {@link Database} node is supported.
     */
    @Override
    public void visit(Database node) {
        // noop
    }

    /**
     {@link MysqlDatabase} node is supported.
     */
    @Override
    public void visit(MysqlDatabase node) {
        // noop
    }

    /**
     {@link Dbms} node is supported.
     */
    @Override
    public void visit(Dbms dbms) {
        // noop
    }

    /**
     {@link MysqlDbms} node is supported.
     */
    @Override
    public void visit(MysqlDbms node) {
        // noop
    }

    /**
     {@link WebApplication} node is supported.
     */
    @Override
    public void visit(WebApplication node) {
        // noop
    }

    /**
     {@link Nodejs} node is supported.
     */
    @Override
    public void visit(Nodejs nodejs) {
        // noop
    }

    @Override
    public void visit(JavaRuntime node) {
        // noop
    }

    @Override
    public void visit(JavaApplication node) {
        // noop
    }
}
