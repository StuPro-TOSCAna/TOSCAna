package org.opentosca.toscana.model.nodedefinition;

import org.opentosca.toscana.core.BaseUnitTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractDefinitionTest extends BaseUnitTest {
    
    @Test
    public void resolveTest(){
        String result = new SoftwareComponentDefinition().resolve("property_name");
        assertEquals("propertyName", result);
        String result2 = new SoftwareComponentDefinition().resolve("another_property_name");
        assertEquals("anotherPropertyName", result2);
        
    }

}
