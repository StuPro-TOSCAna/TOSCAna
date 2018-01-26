package org.opentosca.toscana.model.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class ToscaKey<T> {

    public final String name;
    /**
     Directives can be used in order to enrich the semantics of the key (e.g., used to specify unit sizes)
     */
    private final Map<String, Object> directives = new HashMap<>();
    private ToscaKey<?> predecessor = null;
    private boolean required = false;
    /**
     True if this key denotes a list entity.
     */
    private boolean isList = false;
    private Class type = String.class;

    /**
     Creates a ToscaKey which is not mandatory and of type String
     */
    public ToscaKey(String name) {
        this.name = name;
    }

    /**
     Creates a ToscaKey which is not mandatory of type String.
     */
    public ToscaKey(ToscaKey<?> predecessor, String name) {
        this(name);
        this.predecessor = predecessor;
    }

    public <T> ToscaKey<T> required() {
        this.required = true;
        return (ToscaKey<T>) this;
    }

    public <T> ToscaKey<T> list() {
        this.isList = true;
        return (ToscaKey<T>) this;
    }

    public <T> ToscaKey<T> type(Class type) {
        this.type = type;
        return (ToscaKey<T>) this;
    }

    /**
     Sets a directive

     @see #directives
     */
    public <T> ToscaKey<T> directive(String key, Object value) {
        directives.put(key, value);
        return (ToscaKey<T>) this;
    }

    /**
     @return {@link #predecessor}
     */
    public Optional<ToscaKey<?>> getPredecessor() {
        return Optional.ofNullable(predecessor);
    }

    public boolean hasPredecessor() {
        return (predecessor != null);
    }

    public Map<String, Object> getDirectives() {
        return directives;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isList() {
        return isList;
    }

    public boolean hasSameShape(ToscaKey<?> other) {
        boolean sameName = getName().equals(other.getName());
        boolean samePredecessorShape = getPredecessor().isPresent() && other.getPredecessor().isPresent()
            && getPredecessor().get().hasSameShape(other.getPredecessor().get());
        return sameName && samePredecessorShape;
    }
}
