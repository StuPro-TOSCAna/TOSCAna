package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.util.StatusService;
import org.opentosca.toscana.core.util.SystemStatus;

public class DummySystemStatusProvicer implements StatusService {
	@Override
	public SystemStatus getSystemStatus() {
		return SystemStatus.values()[(int) Math.floor(SystemStatus.values().length*Math.random())];
	}
}
