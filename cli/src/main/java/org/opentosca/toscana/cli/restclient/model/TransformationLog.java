package org.opentosca.toscana.cli.restclient.model;

public class TransformationLog {

	private Long timestamp;
	private String message;
	private String level;

	/**
	 *
	 * @param message
	 * @param timestamp
	 * @param level
	 */
	public TransformationLog(Long timestamp, String message, String level) {
		this.timestamp = timestamp;
		this.message = message;
		this.level = level;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

	public String getLevel() {
		return level;
	}

}
