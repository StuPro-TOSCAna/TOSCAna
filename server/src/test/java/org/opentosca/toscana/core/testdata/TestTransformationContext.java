package org.opentosca.toscana.core.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 Supplies valid TransformationContext objects for plugin integration tests. <br> Note: Internally, uses core
 functionality. Do <b>NOT</b> use this for isolated testing.
 Try to build unit tests instead. Use this only as a last resort.
 */
public class TestTransformationContext extends BaseSpringTest {

    @Autowired
    private TransformationService transformationService;
    @Autowired
    private CsarDao csarDao;
    @Autowired
    private TransformationDao transformationDao;
    @Autowired
    private CsarService csarService;

    /**
     @param csarFile the csar for the context. To obtain a csar file, use TestCsar.`your_csar`
     @param platform the underlying target platform of the context;
     @return a valid TransformationContext object based on given csar and platform
     */
    public TransformationContext getContext(File csarFile, Platform platform) throws FileNotFoundException, InvalidCsarException, PlatformNotFoundException {
        InputStream is = new FileInputStream(csarFile);
        Csar csar = csarService.submitCsar("test-csar", is);
        Transformation transformation = transformationService.createTransformation(csar, platform);

        File csarContentDir = csarDao.getContentDir(csar);
        File transformationContentDir = transformationDao.getContentDir(transformation);
        return new TransformationContext(csarContentDir, transformationContentDir, mock(Log.class),
            csar.getModel().get(), transformation.getProperties());
    }

    @Test
    public void serviceTemplateOfContextNotNull() throws FileNotFoundException, InvalidCsarException, PlatformNotFoundException {
        TransformationContext context = getContext(TestCsars.VALID_EMPTY_TOPOLOGY, TestPlugins.PLATFORM1);
        assertNotNull(context.getModel());
    }
}
