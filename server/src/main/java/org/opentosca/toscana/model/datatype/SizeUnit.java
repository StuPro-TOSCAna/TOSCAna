package org.opentosca.toscana.model.datatype;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 A scalar size unit as defined in the TOSCA spec
 This class only serves as indicator and has no actual functionality
 */
public class SizeUnit {

    // useful to set directives in a ToscaKey
    public static final String TO = "to";
    public static final String FROM = "from";

    private final static Logger logger = LoggerFactory.getLogger(SizeUnit.class);

    private final static Map<Unit, Long> SIZE_MAP = new HashMap<>();

    static {
        SIZE_MAP.put(Unit.B, 1L);
        SIZE_MAP.put(Unit.kB, 1000L);
        SIZE_MAP.put(Unit.KiB, 1024L);
        SIZE_MAP.put(Unit.MB, (long) Math.pow(1000, 2));
        SIZE_MAP.put(Unit.MiB, (long) Math.pow(1024, 2));
        SIZE_MAP.put(Unit.GB, (long) Math.pow(1000, 3));
        SIZE_MAP.put(Unit.GiB, (long) Math.pow(1024, 3));
        SIZE_MAP.put(Unit.TB, (long) Math.pow(1000, 4));
        SIZE_MAP.put(Unit.TiB, (long) Math.pow(1024, 4));
    }

    /**
     Tries to convert arbitrary sizes like "16" or "16 GB" to an integer representation in MB.

     @param from if size does not contain information about unit, size shall be interpreted to be in this unit
     @param to   target size unit
     @return the size in MB or null, if parsing failed
     */
    public static Integer convert(Object size, Unit from, Unit to) {
        Integer result = null;
        Double number = null;
        try {
            if (size instanceof String) {
                from = Unit.valueOf(((String) size).replaceAll("[0-9. ]*", ""));
                String sizeString = ((String) size).replaceAll("[^0-9.]*", "");
                number = Double.valueOf(sizeString);
            } else if (size instanceof Integer) {
                number = Double.valueOf((Integer) size);
            } else if (size instanceof Double) {
                number = (Double) size;
            }
            long factor = SIZE_MAP.get(from);
            result = (int) ((number * factor) / SIZE_MAP.get(to));
        } catch (Exception e) {
            logger.error("Illegal size: '{}'", size, e);
        }

        return result;
    }

    public static enum Unit {
        B,
        kB,
        KiB,
        MB,
        MiB,
        GB,
        GiB,
        TB,
        TiB
    }
}
