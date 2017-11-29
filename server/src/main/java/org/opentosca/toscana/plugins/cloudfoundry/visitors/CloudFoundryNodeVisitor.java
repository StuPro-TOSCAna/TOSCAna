package org.opentosca.toscana.plugins.cloudfoundry.visitors;

import org.opentosca.toscana.model.node.Compute;
import org.opentosca.toscana.model.node.MysqlDatabase;
import org.opentosca.toscana.model.node.MysqlDbms;
import org.opentosca.toscana.model.node.WebApplication;
import org.opentosca.toscana.model.operation.OperationVariable;
import org.opentosca.toscana.model.visitor.NodeVisitor;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryApplication;

public class CloudFoundryNodeVisitor implements NodeVisitor {

    CloudFoundryApplication myApp;
    
    public CloudFoundryNodeVisitor (CloudFoundryApplication myApp) {
        this.myApp = myApp;
    }
    
    @Override
    public void visit(Compute node) {
        /*
        os: do nothing -> CF uses linux based containers
        private_address: do nothing -> automatically
        public_address: add value to manifest
        networks: do nothing -> automatically
        ports: do nothing -> automatically
        */
        
        myApp.addAttribute("domain",node.getPublicAddress().toString());
        myApp.addAttribute("disk-quota", node.getHost().getDiskSizeInMB() + " MB");
        myApp.addAttribute("mem_size", node.getHost().getMemSizeInMB() + " MB");
        
    }

    @Override
    public void visit(MysqlDatabase node) {
        /*
        create service
        ignore password and port
         */
        myApp.addService(node.getNodeName());
    }

    @Override
    public void visit(MysqlDbms node) {
        // TODO: check how to configure database
    }

    @Override
    public void visit(WebApplication node) { 
        myApp.setAppName(node.getNodeName());
        for (OperationVariable inputVariable: node.getStandardLifecycle().getInputs()
             ) {
            myApp.addEnvironmentVariables(inputVariable.getKey(), "");
        }     
        
        //TODO: get files to right folder in output
    }

}
