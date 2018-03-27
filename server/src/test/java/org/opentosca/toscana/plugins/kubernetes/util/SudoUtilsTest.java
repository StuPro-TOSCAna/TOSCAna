package org.opentosca.toscana.plugins.kubernetes.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SudoUtilsTest extends BaseUnitTest {

    private static final String DEBIAN_INSTALL_CMD = "apt-get update && apt-get install -y sudo";
    private static final String ALPINE_INSTALL_CMD = "apk add --update --no-cache sudo";
    private static final String CENTOS_INSTALL_CMD = "yum install -y sudo";

    private String input;
    private String expected;

    public SudoUtilsTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void validate() {
        Optional<String> result = SudoUtils.getSudoInstallCommand(input);
        if (expected == null) {
            assertTrue(!result.isPresent());
            return;
        }
        String r = result.get();
        assertEquals(r, expected);
    }

    @Parameterized.Parameters(name = "{index}: {0}={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"library/alpine", ALPINE_INSTALL_CMD},
            {"library/docker", ALPINE_INSTALL_CMD},
            {"library/ubuntu", DEBIAN_INSTALL_CMD},
            {"library/debian", DEBIAN_INSTALL_CMD},
            {"library/node", DEBIAN_INSTALL_CMD},
            {"library/httpd", DEBIAN_INSTALL_CMD},
            {"library/php", DEBIAN_INSTALL_CMD},
            {"library/mysql", DEBIAN_INSTALL_CMD},
            {"library/centos", CENTOS_INSTALL_CMD},
            {"library/fedora", CENTOS_INSTALL_CMD},
            {"library/randomos", null},
        });
    }
}
