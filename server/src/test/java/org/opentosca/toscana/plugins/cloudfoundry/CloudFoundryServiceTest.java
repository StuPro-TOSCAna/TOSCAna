/* TODO:
package org.opentosca.toscana.plugins.cloudfoundry;

import java.io.File;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.plugin.PluginFileAccess;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryClient.CloudFoundryConnection;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryApplication;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryProvider;
import org.opentosca.toscana.plugins.cloudfoundry.application.CloudFoundryServiceType;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILEPRAEFIX_DEPLOY;
import static org.opentosca.toscana.plugins.cloudfoundry.CloudFoundryFileCreator.FILESUFFIX_DEPLOY;

public class CloudFoundryServiceTest extends BaseUnitTest{
    
    private static CloudFoundryConnection cloudFoundryConnection;
    private CloudFoundryApplication app;
    private CloudFoundryProvider provider;
    private CloudFoundryFileCreator fileCreator;

    @Mock
    private Log log;
    private File targetDir;
    private String appName;
    private final String outputPath = AbstractLifecycle.SCRIPTS_DIR_PATH;
    
    @Before
    public void setUp() {
        appName = "testapp";
        app =new CloudFoundryApplication(appName);
        app.addService("my_db", CloudFoundryServiceType.MYSQL);
        cloudFoundryConnection = new CloudFoundryConnection("jmuell.dev@gmail.com",
            "",
            "api.run.pivotal.io",
            "stupro.toscana",
            "development");
        
        provider = new CloudFoundryProvider(CloudFoundryProvider.CloudFoundryProviderType.PIVOTAL);
        provider.setOfferedService(cloudFoundryConnection.getServices());
        app.setProvider(provider);

        File sourceDir = new File(tmpdir, "sourceDir");
        targetDir = new File(tmpdir, "targetDir");
        sourceDir.mkdir();
        targetDir.mkdir();
        PluginFileAccess fileAccess = new PluginFileAccess(sourceDir, targetDir, log);
        fileCreator = new CloudFoundryFileCreator(fileAccess, app);
    }
    
    @Test
    public void checkSerice() throws Exception {
        fileCreator.createFiles();
        File targetFile = new File(targetDir, outputPath + FILEPRAEFIX_DEPLOY + appName + FILESUFFIX_DEPLOY);
        String deployContent = FileUtils.readFileToString(targetFile);
        String expectedDeployContent = "cleardb";
        //assertEquals(expectedDeployContent, deployContent);
        assertThat(deployContent, CoreMatchers.containsString(expectedDeployContent));
        
    }
}

*/
