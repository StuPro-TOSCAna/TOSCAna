package org.opentosca.toscana.cli.restclient.model;

public class Transformation {
	
	private String progress;
	private String platform;
	private String status;
	
	public Transformation(String progress, String platform, String status) {
		this.progress = progress;
		this.platform = platform;
		this.status = status;
	}

	public String getProgress ()
	{
		return progress;
	}

	public String getPlatform ()
	{
		return platform;
	}

	public String getStatus ()
	{
		return status;
	}
}
	
