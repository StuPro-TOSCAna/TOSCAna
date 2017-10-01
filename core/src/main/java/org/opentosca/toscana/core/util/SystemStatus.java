package org.opentosca.toscana.core.util;

public enum SystemStatus {
	IDLE("idle"),
	TRANSFORMING("transforming"),
	ERROR("error");
	
	private String displayName;

	SystemStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
