package org.opentosca.toscana.util;

@FunctionalInterface
public interface ExceptionAwareVoidFunction<A> {
    void apply(A param) throws Exception;
}
