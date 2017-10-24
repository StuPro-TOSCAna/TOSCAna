package org.opentosca.toscana.core.testdata;

import org.assertj.core.util.Lists;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.dummy.DummyPlugin;
import org.opentosca.toscana.core.dummy.ExecutionDummyPlugin;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestPlugins {

    public static final Platform PLATFORM1 = new Platform("testplatform_one", "the first testplatform");
    public static final Platform PLATFORM2 = new Platform("testplatform_two", "the second testplatform");
    public static final Platform PLATFORM3 = new Platform("testplatform_three", "the third testplatform");
    public static final Platform PLATFORM4 = new Platform("testplatform_four", "the forth testplatform");

    public static final TransformationPlugin PLUGIN1 = new DummyPlugin(PLATFORM1);
    public static final TransformationPlugin PLUGIN2 = new DummyPlugin(PLATFORM2);
    public static final TransformationPlugin PLUGIN3 = new DummyPlugin(PLATFORM3);
    public static final TransformationPlugin PLUGIN4 = new DummyPlugin(PLATFORM4);

    public static final ExecutionDummyPlugin PASSING_DUMMY = new ExecutionDummyPlugin("passing", false);
    public static final ExecutionDummyPlugin FAILING_DUMMY = new ExecutionDummyPlugin("failing", true);

    public static final List<Platform> PLATFORMS = Lists.newArrayList(PLATFORM1, PLATFORM2, PLATFORM3, PLATFORM4);
    public static final List<TransformationPlugin> PLUGINS = Lists.newArrayList(PLUGIN1, PLUGIN2, PLUGIN3, PLUGIN4, PASSING_DUMMY, FAILING_DUMMY);

    /**
     * In given csarTransformationsDir, creates for every given target platform a fake transformation on disk
     * Attention: Best is to use platforms which are supported by a plugin -- use TestPlugins.PLATFORMS
     * @param csarTransformationsDir the absolute path of {csarid}/transformations
     * @param targetPlatforms
     */
    public static void createFakeTransformationsOnDisk(File csarTransformationsDir, List<Platform> targetPlatforms) {
        for (Platform platform : targetPlatforms){
            File transformationDir = new File(csarTransformationsDir, platform.id);
            transformationDir.mkdir();
        }
        

    }

}
