package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.embedded.CsarResources;

import static org.junit.Assert.assertEquals;

public class CsarsValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        CsarResources csars = (CsarResources) object;
        assertEquals(3, csars.getContent().size());
        assertEquals(3,
            csars.getContent().stream().filter(e -> e.getName() != null && e.getLinks().size() == 2).count());
        assertEquals(1, csars.getLinks().size());
    }
}
