package org.opentosca.toscana.model.datatype;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;

/**
 Describes port specifications for a network connection.
 <p>
 (TOSCA Simple Profile in YAML Version 1.1, p. 145)
 */
@Data
public class PortSpec {

    /**
     The protocol used on the port. Defaults to {@link PortProtocol#TCP}.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 145)
     */
    public final PortProtocol protocol;

    /**
     The optional source port.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 145)
     */
    public final Port source;

    /**
     The optional range the for source port.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 146)
     */
    public final Range sourceRange;

    /**
     The optional target port.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 146)
     */
    public final Port target;
    /**
     The optional range for the target port.
     <p>
     (TOSCA Simple Profile in YAML Version 1.1, p. 146)
     */
    public final Range targetRange;

    @Builder
    private PortSpec(PortProtocol protocol,
                     Port source,
                     Range sourceRange,
                     Port target,
                     Range targetRange) {
        this.protocol = (protocol == null) ? PortProtocol.TCP : protocol;
        if (source == null && sourceRange == null && target == null && targetRange == null) {
            throw new IllegalArgumentException("One of (source, sourceRange, target, targetRange) must be not null.");
        }
        if (source != null && !sourceRange.inRange(source.port)) {
            throw new IllegalArgumentException(String.format("source port (%d) must be" +
                " in range of sourceRange (%d,%d)", source.port, sourceRange.min, sourceRange.max));
        }
        if (target != null && !targetRange.inRange(source.port)) {
            throw new IllegalArgumentException(String.format("target port (%d) must be" +
                " in range of targetRange (%d,%d)", target.port, targetRange.min, targetRange.max));
        }
        this.source = source;
        this.sourceRange = sourceRange;
        this.target = target;
        this.targetRange = targetRange;
    }

    /**
     @return {@link #source}
     */
    public Optional<Port> getSource() {
        return Optional.ofNullable(source);
    }

    /**
     @return {@link #sourceRange}
     */
    public Optional<Range> getSourceRange() {
        return Optional.ofNullable(sourceRange);
    }

    /**
     @return {@link #target}
     */
    public Optional<Port> getTarget() {
        return Optional.ofNullable(target);
    }

    /**
     @return {@link #targetRange}
     */
    public Optional<Range> getTargetRange() {
        return Optional.ofNullable(targetRange);
    }

    public enum PortProtocol {
        TCP,
        UDP,
        IGMP
    }
}
