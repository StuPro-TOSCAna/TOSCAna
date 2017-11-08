package org.opentosca.toscana.cli.restclient.model;

public class Platform {

	private String id;
	private String name;
	
	/**
	 * JSON for specific Platform gets converted back to a Java Object
	 * @param id
	 * @param name
	 */
	public Platform(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
