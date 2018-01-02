package org.opentosca.toscana.model.datatype;

import java.security.InvalidParameterException;

import static java.lang.String.format;

/**
 Specifies a range of occurrences.
 */
public class Range {

    public static final Range EXACTLY_ONCE = new Range(1, 1);
    public static final Range AT_LEAST_ONCE = new Range(1, Integer.MAX_VALUE);
    public static final Range ANY = new Range(0, Integer.MAX_VALUE);

    public final int min;
    public final int max;

    /**
     @param min the minimum allowed number of occurrences.
     @param max the maximum allowed number of occurrences. Use Integer.MAX_VALUE to indicate `UNBOUNDED`.
     Must not be less than min
     */
    public Range(int min, int max) {
        if (max < min) {
            throw new InvalidParameterException(format("Constraint violation: min (%d) <= max (%d)", min, max));
        }
        if (max < 0 || min < 0) {
            throw new InvalidParameterException(format("Constraint violation: min (%d) >= 0 && max (%d) >= 0", min, max));
        }
        this.min = min;
        this.max = max;
    }

    /**
     @return <code>true</code> if (min <= value <= max), <code>false</code> otherwise
     */
    public boolean inRange(int value) {
        return (min <= value && value <= max);
    }

    @Override
    public String toString() {
        return format("[Range:%s-%s]", min, max);
    }
}
