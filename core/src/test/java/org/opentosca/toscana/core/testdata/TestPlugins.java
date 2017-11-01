package org.opentosca.toscana.core.testdata;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.testdata.dummyplugins.DummyPlugin;
import org.opentosca.toscana.core.testdata.dummyplugins.ExecutionDummyPlugin;
import org.opentosca.toscana.core.testdata.dummyplugins.FileCreationExcecutionDummy;
import org.opentosca.toscana.core.transformation.platform.Platform;

import org.assertj.core.util.Lists;

public class TestPlugins {

    public static final Platform PLATFORM1 = new Platform("testplatform_one", "the first testplatform");
    public static final TransformationPlugin PLUGIN1 = new DummyPlugin(PLATFORM1);

    public static final Platform PLATFORM2 = new Platform("testplatform_two", "the second testplatform");
    public static final TransformationPlugin PLUGIN2 = new DummyPlugin(PLATFORM2);

    public static final Platform PLATFORM3 = new Platform("testplatform_three", "the third testplatform");
    public static final TransformationPlugin PLUGIN3 = new DummyPlugin(PLATFORM3);

    public static final Platform PLATFORM4 = new Platform("testplatform_four", "the forth testplatform");
    public static final TransformationPlugin PLUGIN4 = new DummyPlugin(PLATFORM4);

    public static final Platform PLATFORM_NOT_SUPPORTED = new Platform("not-supported-plattform",
        "this platform is not supported by any plugin");

    public static final Platform PLATFORM_PASSING_DUMMY = new Platform("testplatform_passing_dummy",
        "the testplatform for passing dummy");
    public static final ExecutionDummyPlugin PASSING_DUMMY =
        new ExecutionDummyPlugin(PLATFORM_PASSING_DUMMY, false);

    public static final Platform PLATFORM_FAILING_DUMMY = new Platform("testplatform_failing_dummy",
        "the testplatform for passing dummy");
    public static final ExecutionDummyPlugin FAILING_DUMMY =
        new ExecutionDummyPlugin(PLATFORM_FAILING_DUMMY, true);

    public static final Platform PLATFORM_PASSING_WRITING_DUMMY = new Platform("testplatform_passing_writing_dummy",
        "the testplatform for passing writing dummy");
    public static final ExecutionDummyPlugin PASSING_WRITING_DUMMY =
        new FileCreationExcecutionDummy(PLATFORM_PASSING_WRITING_DUMMY, false);

    public static final Platform PLATFORM_FAILING_WRITING_DUMMY = new Platform("testplatform_failing_writing_dummy",
        "the testplatform for failing writing dummy");
    public static final ExecutionDummyPlugin FAILING_WRITING_DUMMY =
        new FileCreationExcecutionDummy(PLATFORM_FAILING_WRITING_DUMMY, true);

    public static final Set<Platform> PLATFORMS = new HashSet<>(Arrays.asList(PLATFORM1, PLATFORM2, PLATFORM3,
        PLATFORM4, PLATFORM_FAILING_DUMMY, PLATFORM_FAILING_WRITING_DUMMY, PLATFORM_PASSING_DUMMY, PLATFORM_PASSING_WRITING_DUMMY));
    public static final List<TransformationPlugin> PLUGINS = Lists.newArrayList(
        PLUGIN1, PLUGIN2,
        PLUGIN3, PLUGIN4,
        PASSING_DUMMY, FAILING_DUMMY,
        PASSING_WRITING_DUMMY, FAILING_WRITING_DUMMY
    );

    /**
     In given csarTransformationsDir, creates for every given target platform a fake transformation on disk <p>
     Attention: Best is to use platforms which are supported by a plugin -- use TestPlugins.PLATFORMS

     @param csarTransformationsDir the absolute path of {csarid}/transformations
     */
    public static void createFakeTransformationsOnDisk(File csarTransformationsDir, Set<Platform> targetPlatforms) {
        for (Platform platform : targetPlatforms) {
            File transformationDir = new File(csarTransformationsDir, platform.id);
            transformationDir.mkdir();
        }
    }
}
