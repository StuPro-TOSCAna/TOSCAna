package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.opentosca.toscana.core.BaseUnitTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarImpl;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.opentosca.toscana.core.util.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentosca.toscana.core.testdata.TestPlugins.PLATFORM1;

public class TransformationDaoUnitTest extends BaseUnitTest {

    @Mock
    private Preferences preferences;
    @Mock
    private PlatformService platformService;
    @Mock
    private CsarDao csarDao;
    private File platformDir;
    private Transformation transformation;
    private Csar csar;
    private TransformationDao dao;

    @Before
    public void setUp() throws IOException {
        when(platformService.findPlatformById(PLATFORM1.id)).thenReturn(Optional.ofNullable(PLATFORM1));
        when(preferences.getDataDir()).thenReturn(tmpdir);
        csar = new CsarImpl("csarIdentifier", mock(Log.class));
        File csarTransformationDir = new File(tmpdir, "transformationDir");
        csarTransformationDir.mkdir();
        when(csarDao.getTransformationsDir(csar)).thenReturn(csarTransformationDir);
        platformDir = new File(csarTransformationDir, PLATFORM1.id);
        platformDir.mkdir();
        File someTransformationFile = new File(platformDir, "some-file");
        someTransformationFile.createNewFile();

        dao = new TransformationFilesystemDao(platformService);
        dao.setCsarDao(csarDao);
    }

    @Test
    public void initializeTransformationStateDone() throws IOException {
        // create fake transformation on disk
        String successfulArtifactName = "csarname-platformid_somedate.zip";
        // assert filename for test data is correct
        assertTrue(successfulArtifactName.matches(TransformationFilesystemDao.ARTIFACT_SUCCESSFUL_REGEX));
        File artifactFileSuccess = new File(platformDir, successfulArtifactName);
        artifactFileSuccess.createNewFile();

        transformation = dao.find(csar, PLATFORM1).get();

        assertEquals(TransformationState.DONE, transformation.getState());
        assertTrue(transformation.getTargetArtifact().isPresent());
    }

    @Test
    public void initializeTransformationStateError() throws IOException {
        // create fake failed transformation on disk
        String failedArtifactName = "csarname-platformid_somedate_failed.zip";
        assertTrue(failedArtifactName.matches(TransformationFilesystemDao.ARTIFACT_FAILED_REGEX));
        File artifactFileFailed = new File(platformDir, failedArtifactName);
        artifactFileFailed.createNewFile();

        transformation = dao.find(csar, PLATFORM1).get();

        assertEquals(TransformationState.ERROR, transformation.getState());
        assertTrue(transformation.getTargetArtifact().isPresent());
    }
}
