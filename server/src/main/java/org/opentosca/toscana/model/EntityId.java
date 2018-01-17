package org.opentosca.toscana.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opentosca.toscana.model.util.ToscaKey;

public class EntityId implements Comparable<EntityId> {

    public static final String WILDCARD = "*";
    private final List<String> path;
    private final String name;

    public EntityId(List<String> path) {
        this.path = Collections.unmodifiableList(new ArrayList<>(path));
        if (path.isEmpty()) {
            this.name = "";
        } else {
            this.name = path.get(path.size() - 1);
        }
    }

    @Override
    public int compareTo(EntityId other) {
        if (path.size() > other.path.size()) {
            return 1;
        } else if (path.size() < other.path.size()) {
            return -1;
        } else {
            int result = 0;
            for (int i = 0; i < path.size(); i++) {
                String segment = path.get(i);
                String otherSegment = other.path.get(i);
                result = segment.compareTo(otherSegment);
                if (result != 0) {
                    return result;
                }
            }
        }
        return 0;
    }

    public List<String> getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    /**
     Returns a new EntityId which descends from this EntityId. If the path of this entity is [seg1, seg],
     the path of the new entity will be [seg1, seg2, lastSegment]

     @param lastSegment the last segment which is appended to the path
     */
    public EntityId descend(String lastSegment) {
        List<String> newPath = new ArrayList<>(this.path);
        newPath.add(lastSegment);
        return new EntityId(newPath);
    }

    public EntityId descend(ToscaKey<?> key) {
        EntityId intermediateId = this;
        if (key.hasPredecessor()) {
            String predecessor = key.getPredecessor().get().getName();
            intermediateId = descend(predecessor);
        }
        return intermediateId.descend(key.getName());
    }

    public EntityId ascend() {
        int pathSize = this.path.size();
        pathSize = (pathSize < 2) ? 2 : pathSize;
        List<String> parentPath = this.path.subList(0, pathSize - 1);
        return new EntityId(parentPath);
    }

    @Override
    public String toString() {
        String representation = "";
        for (int i = 0; i < path.size(); i++) {
            representation += path.get(i);
            if (i != path.size() - 1) {
                representation += " > ";
            }
        }
        return representation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityId entityId = (EntityId) o;

        return path.equals(entityId.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
