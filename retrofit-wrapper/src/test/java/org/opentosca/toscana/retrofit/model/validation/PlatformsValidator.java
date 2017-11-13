package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.embedded.PlatformResources;

import static org.junit.Assert.assertEquals;

public class PlatformsValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        PlatformResources platforms = (PlatformResources) object;
        assertEquals(3, platforms.getContent().size());
        assertEquals(3,
            platforms.getContent().stream().filter(
                e -> e.getId() != null && e.getName() != null && e.getLinks().size() == 1
            ).count());
    }
}
