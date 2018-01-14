package org.opentosca.toscana.model.util;

import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class ToscaKey<T> {

    public final String name;
    private ToscaKey<?> predecessor;
    private boolean required;
    private Class type;

    /**
     Creates a ToscaKey which is not mandatory and of type String
     */
    public ToscaKey(String name) {
        this.name = name;
        this.type = String.class;
        this.required = false;
        this.predecessor = null;
    }

    /**
     Creates a ToscaKey which is not mandatory of type String.
     */
    public ToscaKey(ToscaKey<?> predecessor, String name) {
        this(name);
        this.predecessor = predecessor;
    }

    public <T> ToscaKey<T> required(boolean required) {
        this.required = required;
        return (ToscaKey<T>) this;
    }

    public <T> ToscaKey<T> type(Class type) {
        this.type = type;
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
}
