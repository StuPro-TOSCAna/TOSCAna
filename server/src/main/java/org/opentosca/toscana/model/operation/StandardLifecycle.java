package org.opentosca.toscana.model.operation;

import java.util.Optional;

import org.opentosca.toscana.core.parse.model.MappingEntity;
import org.opentosca.toscana.model.util.ToscaKey;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 see TOSCA Specification V1.1 ch. 5.8.4
 */
@EqualsAndHashCode
@ToString
public class StandardLifecycle extends Interface {

    /**
     The create operation is generally used to create the resource or service its node represents in the topology.
     */
    public static ToscaKey<Operation> CREATE = new ToscaKey<>("create").type(Operation.class);
    public static ToscaKey<Operation> CONFIGURE = new ToscaKey<>("configure").type(Operation.class);
    public static ToscaKey<Operation> START = new ToscaKey<>("start").type(Operation.class);
    public static ToscaKey<Operation> STOP = new ToscaKey<>("stop").type(Operation.class);
    public static ToscaKey<Operation> DELETE = new ToscaKey<>("delete").type(Operation.class);

    public StandardLifecycle(MappingEntity entity) {
        super(entity);
    }

    /**
     @return {@link #CREATE}
     */

    public Optional<Operation> getCreate() {
        return Optional.ofNullable(get(CREATE));
    }

    /**
     Sets {@link #CREATE}
     */
    public StandardLifecycle setCreate(Operation create) {
        set(CREATE, create);
        return this;
    }

    /**
     @return {@link #CONFIGURE}
     */

    public Optional<Operation> getConfigure() {
        return Optional.ofNullable(get(CONFIGURE));
    }

    /**
     Sets {@link #CONFIGURE}
     */
    public StandardLifecycle setConfigure(Operation configure) {
        set(CONFIGURE, configure);
        return this;
    }

    /**
     @return {@link #START}
     */

    public Optional<Operation> getStart() {
        return Optional.ofNullable(get(START));
    }

    /**
     Sets {@link #START}
     */
    public StandardLifecycle setStart(Operation start) {
        set(START, start);
        return this;
    }

    /**
     @return {@link #STOP}
     */

    public Optional<Operation> getStop() {
        return Optional.ofNullable(get(STOP));
    }

    /**
     Sets {@link #STOP}
     */
    public StandardLifecycle setStop(Operation stop) {
        set(STOP, stop);
        return this;
    }

    /**
     @return {@link #DELETE}
     */

    public Optional<Operation> getDelete() {
        return Optional.ofNullable(get(DELETE));
    }

    /**
     Sets {@link #DELETE}
     */
    public StandardLifecycle setDelete(Operation delete) {
        set(DELETE, delete);
        return this;
    }
}
