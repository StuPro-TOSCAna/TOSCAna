package org.opentosca.toscana.retrofit.model.validation;

import org.opentosca.toscana.retrofit.model.TransformerStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HealthValidator implements IModelValidator {
    @Override
    public void validate(Object object) {
        TransformerStatus status = (TransformerStatus) object;
        assertEquals("UP", status.getStatus());

        //Check transformer Health
        TransformerStatus.TransformerHealth transformer = status.getTransformerHealth();
        assertEquals("UP",transformer.getStatus());
        assertEquals(3, transformer.getInstalledPlugins().size());
        assertEquals(1,
            transformer.getErroredTransformations()
                .stream().filter(e -> e.getCsarName() != null && e.getPlatformName() != null).count());
        assertEquals(1,
            transformer.getRunningTransformations()
                .stream().filter(e -> e.getCsarName() != null && e.getPlatformName() != null).count());

        //Check FS health
        TransformerStatus.FileSystemHealth fs = status.getFileSystemHealth();
        assertEquals("UP", fs.getStatus());
        assertTrue("Free bytes is smaller than 0", fs.getFreeBytes() > 0);
        assertTrue("total bytes is smaller than 0", fs.getTotalBytes() > 0);
        assertTrue("threshold bytes is smaller than 0", fs.getThreshold() > 0);
    }
}
