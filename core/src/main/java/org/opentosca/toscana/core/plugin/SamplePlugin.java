package org.opentosca.toscana.core.plugin;

import org.opentosca.toscana.core.transformation.platform.Platform;
import org.springframework.stereotype.Controller;

import java.util.HashSet;

@Controller
public class SamplePlugin extends AbstractPlugin{
	@Override
	public Platform getPlatformDetails() {
		return new Platform("sample","Sample Plugin", new HashSet<>());
	}
}
