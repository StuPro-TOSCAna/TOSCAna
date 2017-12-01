package org.opentosca.toscana.plugins.cloudformation;

import java.io.IOException;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.cloudformation.builder.RootTemplate;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class CloudFormationLifecycle extends AbstractLifecycle {

    private RootTemplate rootTemplate;
    
    
    public CloudFormationLifecycle(TransformationContext context) throws IOException {
        super(context);
    }

    @Override
    public boolean checkModel() {
        return true;
    }

    @Override
    public void prepare() {
        this.rootTemplate = new RootTemplate();
    }

    @Override
    public void transform() {
        logger.info(getTemplateAsString(rootTemplate));
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException();
    }

    public String getTemplateAsString(RootTemplate rootTemplate){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootTemplate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
