package org.opentosca.toscana.core.parse.model;

public class Connection {

    private final Entity source;
    private final Entity target;
    private final String key;

    public Connection(String key, Entity source, Entity target) {
        this.key = key;
        this.source = source;
        this.target = target;
    }

    public Entity getSource() {
        return source;
    }

    public Entity getTarget() {
        return target;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Connection that = (Connection) o;

        if (!source.equals(that.source)) return false;
        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + target.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s ==%s=>> %s", source.getId(), key, target.getId());
    }
}
