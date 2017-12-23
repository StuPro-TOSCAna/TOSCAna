package org.opentosca.toscana.model;

import org.opentosca.toscana.model.node.RootNode;
import org.opentosca.toscana.model.nodedefinition.BaseDefinition;

import lombok.Builder;

@Builder
public abstract class ToscaEntity {

    protected final PropertyLinker propertyLinker;

    public ToscaEntity() {
        this.propertyLinker = new PropertyLinker(this, getDefinition());
    }

    /**
     Establishes a link between this instance's property (defined by sourceField) and the targets targetField.
     In other words, the linked property now behaves like a symbolic link.
     */
    public void link(String toscaSourceFieldName, RootNode target, String toscaTargetFieldName) {
        propertyLinker.link(toscaSourceFieldName, target, toscaTargetFieldName);
    }

    protected <T> T get(String propertyName) {
        return propertyLinker.resolveGet(propertyName);
    }

    protected <T> void set(String propertyName, T value) {
        propertyLinker.resolveSet(propertyName, value);
    }

    /**
     Overwrite this to return the definition for the specific subtype. Definition instance shall contain the all
     tosca key names of the corresponding tosca entity including their custom mappings to the classes property, if necessary.
     */
    protected BaseDefinition getDefinition() {
        return new BaseDefinition();
    }

    public static class ToscaEntityBuilder {
        
        public ToscaEntityBuilder(){
        }

        public ToscaEntity build() {
            // should never happen
            throw new IllegalStateException();
        }
    }
}
