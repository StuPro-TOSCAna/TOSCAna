package org.opentosca.toscana.core.parse.model;

import java.util.Collection;

public interface CollectionEntity {

    Collection<Entity> getChildren();

    void addChild(Entity entity);
}
