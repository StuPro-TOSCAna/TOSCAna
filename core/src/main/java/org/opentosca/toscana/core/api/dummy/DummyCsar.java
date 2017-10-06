package org.opentosca.toscana.core.api.dummy;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;

import java.util.HashMap;
import java.util.Map;

public class DummyCsar implements Csar{
	
	private String name;
	private byte[] data;
	

	public DummyCsar(String name) {
		this.name = name;
	}

	@Override
	public Map<String, Transformation> getTransformations() {
		return new HashMap<>();
	}

	@Override
	public String getIdentifier() {
		return name;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
