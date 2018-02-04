package org.opentosca.toscana.core.transformation.logging;

/**
 Util class for unifying log message formatting
 */
public class LogFormat {

    public final static String[] INDENT_LEVELS = {"", "  > ", "    > "};

    public static String pointAt(int indentLevel, Object source, Object target) {
        return String.format("%s%-85s  ===> %s", INDENT_LEVELS[indentLevel], source, target);
    }

    public static String pointAt(int indentLevel, int padding, Object source, Object connection, Object target) {
        return String.format("%s%-" + padding + "s  === %-10s ==> %s", INDENT_LEVELS[indentLevel], source, connection, target);
    }

    public static String indent(int indentLevel, Object object) {
        return String.format("%s%s", INDENT_LEVELS[indentLevel], object);
    }
}
