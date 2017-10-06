package org.opentosca.toscana.core.csar;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opentosca.toscana.core.transformation.Platform;
import org.opentosca.toscana.core.transformation.Transformation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class CsarImpl implements Csar{

    /**
     * Stores all scheduled, ongoing or finished transformations of this CSAR.
     * Key is the platform identifier.
     */
    private Map<String, Transformation> transformations = new HashMap<>();
    private String identifier;

    public CsarImpl(String identifier) {
        this.identifier = identifier;
    }

    public void transform(Platform targetPlatform) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Transformation> getTransformations() {
        return transformations;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }


	@Override
	public boolean equals(Object obj) {
    	return (obj instanceof Csar) && (((Csar) obj).getIdentifier().equals(identifier));
	}

	@Override
	public int hashCode() {
            return new HashCodeBuilder(17,31)
				.append(identifier)
				.toHashCode();
	}
}

