package org.opentosca.toscana.core.testdata;

import org.opentosca.toscana.core.dummy.DummyPlugin;
import org.opentosca.toscana.core.dummy.ExecutionDummyPlugin;
import org.opentosca.toscana.core.plugin.TransformationPlugin;
import org.opentosca.toscana.core.transformation.platform.Platform;

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
    
    public static final List<TransformationPlugin> PLUGINS = new ArrayList<>();
    
    static {
        PLUGINS.add(PLUGIN1);
        PLUGINS.add(PLUGIN2);
        PLUGINS.add(PLUGIN3);
        PLUGINS.add(PLUGIN4);
        PLUGINS.add(PASSING_DUMMY);
        PLUGINS.add(FAILING_DUMMY);
    }

}
