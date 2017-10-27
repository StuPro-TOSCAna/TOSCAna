package org.opentosca.toscana.core;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public abstract class BaseJUnitTest extends BaseTest {

    @Rule
    protected TemporaryFolder temporaryFolder = new TemporaryFolder();
}
