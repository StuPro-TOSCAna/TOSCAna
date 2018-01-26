package org.opentosca.toscana.model;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.model.datatype.SizeUnit;
import org.opentosca.toscana.model.datatype.SizeUnit.Unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SizeUnitTest extends BaseUnitTest {

    @Test
    public void convertString() {
        Integer result = SizeUnit.convert("16 GB", null, Unit.GB);
        assertEquals(Integer.valueOf(16), result);
        result = SizeUnit.convert("16GB", null, Unit.MB);
        assertEquals(Integer.valueOf(16000), result);
    }

    @Test
    public void convertInteger() {
        Integer result = SizeUnit.convert(new Integer(2), Unit.TB, Unit.MB);
        assertEquals(new Integer(2000000), result);
    }

    @Test
    public void convertDouble() {
        Integer result = SizeUnit.convert(new Double(0.4), Unit.GB, Unit.MB);
        assertEquals(new Integer(400), result);
    }
}
