package org.opentosca.toscana.core.parse.converter.util;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.parse.converter.util.SizeConverter.Unit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SizeConverterTest extends BaseUnitTest {

    private SizeConverter converter;

    @Before
    public void setUp() {
        converter = new SizeConverter();
    }

    @Test
    public void convertString() {
        Integer result = converter.convert("16 GB", null, Unit.GB);
        assertEquals(Integer.valueOf(16), result);
        result = converter.convert("16GB", null, Unit.MB);
        assertEquals(Integer.valueOf(16000), result);
    }

    @Test
    public void convertInteger() {
        Integer result = converter.convert(new Integer(2), Unit.TB, Unit.MB);
        assertEquals(new Integer(2000000), result);
    }

    @Test
    public void convertDouble() {
        Integer result = converter.convert(new Double(0.4), Unit.GB, Unit.MB);
        assertEquals(new Integer(400), result);
    }
}
