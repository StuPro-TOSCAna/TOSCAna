package org.opentosca.toscana.core.testdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.csar.CsarService;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.core.transformation.TransformationDao;
import org.opentosca.toscana.core.transformation.TransformationService;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * Supplies valid TransformationContext objects for plugin integration tests. <br> Note: Internally, uses core
 * functionality. Do <b>NOT</b> use this for isolated testing.
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
     * @param csarFile the csar for the context. To obtain a csar file, use TestCsar.`your_csar`
     * @param platform the underlying target platform of the context;
     * @return a valid TransformationContext object based on given csar and platform
     */
    public TransformationContext getContext(File csarFile, Platform platform) throws FileNotFoundException, InvalidCsarException, PlatformNotFoundException {
        InputStream is = new FileInputStream(csarFile);
        Csar csar = csarService.submitCsar("test-csar", is);
        Transformation transformation = transformationService.createTransformation(csar, platform);

        File csarContentRoot = csarDao.getContentDir(csar);
        File transformationRoot = transformationDao.getRootDir(transformation);
        TransformationContext context = new TransformationContext(transformation, csarContentRoot, transformationRoot);
        return context;
    }

    @Test
    public void serviceTemplateOfContextNotNull() throws FileNotFoundException, InvalidCsarException, PlatformNotFoundException {
        TransformationContext context = getContext(TestCsars.CSAR_YAML_VALID_DOCKER_SIMPLETASK, TestPlugins.PLATFORM1);
        assertNotNull(context.getServiceTemplate());
    }
}
