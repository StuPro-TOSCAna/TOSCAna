package org.opentosca.toscana.core.transformation;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.slf4j.Logger;

public final class TransformationContext {
    private Transformation transformation;
    private PluginFileAccess access;

    public PluginFileAccess getPluginFileAccess() {
        return access;
    }

    protected TransformationContext(Transformation transformation, PluginFileAccess access) {
        this.transformation = transformation;
        this.access = access;

    }

    public TServiceTemplate getServiceTemplate() {
        return transformation.getCsar().getTemplate();
    }

    public Logger getLogger(String context) {
        return transformation.getLog().getLogger(context);
    }
    
    public Logger getLogger(Class clazz){
        return transformation.getLog().getLogger(clazz);
    }
}
