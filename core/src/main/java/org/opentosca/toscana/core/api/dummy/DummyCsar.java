package org.opentosca.toscana.core.api.dummy;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.Transformation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DummyCsar implements Csar{
	
	private String name;
	private byte[] data;

	private Map<String, Transformation> transformations = new HashMap<>();

	public DummyCsar(String name) {
		this.name = name;
	}

	@Override
	public Map<String, Transformation> getTransformations() {
		return transformations;
	}

	@Override
	public String getIdentifier() {
		return name;
	}

	@Override
	public TServiceTemplate getTemplate() {
		return null;
	}

	@Override
	public void setTemplate(TServiceTemplate template) { }

	@Override
	public File getRoot() {
		return null;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
