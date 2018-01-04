package org.opentosca.toscana.model.operation;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 see TOSCA Specification V1.1 ch. 5.8.4
 */
@Data
public class StandardLifecycle extends Interface {

    /**
     The create operation is generally used to create the resource or service its node represents in the topology.
     */
    private final Operation create;
    private final Operation configure;
    private final Operation start;
    private final Operation stop;
    private final Operation delete;

    @Builder
    public StandardLifecycle(Operation create,
                             Operation configure,
                             Operation start,
                             Operation stop,
                             Operation delete,
                             @Singular Set<OperationVariable> inputs) {
        super(inputs,
            Sets.newHashSet(create, configure, start, stop, delete));
        this.create = create;
        this.configure = configure;
        this.start = start;
        this.stop = stop;
        this.delete = delete;
    }

    /**
     @return {@link #create}
     */
    public Optional<Operation> getCreate() {
        return Optional.ofNullable(create);
    }

    /**
     @return {@link #configure}
     */
    public Optional<Operation> getConfigure() {
        return Optional.ofNullable(configure);
    }

    /**
     @return {@link #start}
     */
    public Optional<Operation> getStart() {
        return Optional.ofNullable(start);
    }

    /**
     @return {@link #stop}
     */
    public Optional<Operation> getStop() {
        return Optional.ofNullable(stop);
    }

    /**
     @return {@link #delete}
     */
    public Optional<Operation> getDelete() {
        return Optional.ofNullable(delete);
    }

    public static class StandardLifecycleBuilder extends InterfaceBuilder {
    }
}
