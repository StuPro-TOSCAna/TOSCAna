package org.opentosca.toscana.cli.restclient.model;

/**
 * JSON for Status gets converted back to a Java Object
 */
public class Status {
	
	private String status;
	private Long availableStorage;
	private Long totalStorage;

	public Status(String status, Long availableStorage, Long totalStorage) {
		this.status = status;
		this.availableStorage = availableStorage;
		this.totalStorage = totalStorage;
	}
		
	public String getStatus() {
			return status;
		}
	
	public Long getAvailableStorage() {
			return availableStorage;
		}
		
	public Long getTotalStorage() {
			return totalStorage;
		}
}
	

