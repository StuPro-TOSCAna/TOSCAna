package org.opentosca.toscana.core.transformation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Property<T> {

    public static final Set<Class> SUPPORTED_TYPES = new HashSet<>(Arrays.asList(String.class, Integer.class, Float.class));

    public final Class<T> type;
    public final String key;
    private T value;
    private boolean valid = false;

    Property(Class<T> type, String key) {
        if (!SUPPORTED_TYPES.contains(type)) {
            throw new IllegalArgumentException();
        }
        this.type = type;
        this.key = key;
    }

    /**
     * @param value the value of the property
     * @return <code>true</code> if new value is valid, <code>false</code> otherwise
     */
    public boolean setValue(T value) {
        this.value = value;
        this.valid = (value != null);
        return valid;
    }

    /**
     * @return the value of this property. returns null if not set yet.
     */
    public T getValue() {
        return value;
    }

    /**
     * @return <code>true</code> if value of property is valid, <code>false</code> otherwise
     */
    public boolean isValid() {
        return valid;
    }
}
