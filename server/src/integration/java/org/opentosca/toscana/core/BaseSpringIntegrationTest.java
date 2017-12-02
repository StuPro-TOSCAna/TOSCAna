package org.opentosca.toscana.core;

import org.opentosca.toscana.IntegrationTest;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.CoreConfiguration;
import org.opentosca.toscana.core.Main;

import org.junit.experimental.categories.Category;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.opentosca.toscana.core.testdata.TestProfiles.INTEGRATION_TEST_PROFILE;

/**
 This Subclass of the BaseSpringTest sets the context to the same as you would get when executing the application 
 within the main module.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = {Main.class, CoreConfiguration.class})
@ActiveProfiles(INTEGRATION_TEST_PROFILE)
@Category(IntegrationTest.class)
public abstract class BaseSpringIntegrationTest extends BaseSpringTest {

    /**
     The port the server is bound to.
     
     connecting to it can be done using the URL:
     <code>"http://127.0.0.1:" + port + "/"</code>
     Using localhost is not recomended, as it may cause issues if the hostfile isn't
     setup to handle localhost (thats the case in many docker images)
     */
    @LocalServerPort
    protected int port;

    /**
     @return the local (127.0.0.1) URL to the server int he schema <code>http://127.0.0.1:<PORT>/</code>
     */
    protected String getHttpUrl() {
        return "http://127.0.0.1:" + port + "/";
    }
}
