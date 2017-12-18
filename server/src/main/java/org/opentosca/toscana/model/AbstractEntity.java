package org.opentosca.toscana.model;

import lombok.Builder;

@Builder
public abstract class AbstractEntity {

    public AbstractEntity() {
    }

    public static class AbstractEntityBuilder {
        public AbstractEntityBuilder() {
        }

        public AbstractEntity build() {
            // should never happen
            throw new IllegalStateException();
        }

        public String toString() {
            return "AbstractEntity.AbstractEntityBuilder()";
        }
    }
}
