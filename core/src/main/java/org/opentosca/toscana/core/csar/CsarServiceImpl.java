package org.opentosca.toscana.core.csar;


import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.opentosca.toscana.core.parse.CsarParser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.List;

public class CsarServiceImpl implements CsarService {

	private final CsarDao csarDao;
	private final CsarParser csarParser;
	
	@Autowired
	public CsarServiceImpl(CsarDao dao, CsarParser parser){
		this.csarDao = dao;
		this.csarParser = parser;
	}

	@Override
	public Csar submitCsar(String identifier, InputStream csarStream) {
		Csar csar = csarDao.create(identifier, csarStream);
		populateWithTemplate(csar);
		return csar;
	}

	private void populateWithTemplate(Csar csar) {
		if (csar.getTemplate() == null){
			TServiceTemplate template = csarParser.parse(csar);
			csar.setTemplate(template);
		}
	}

	@Override
	public void deleteCsar(Csar csar) {
		csarDao.delete(csar.getIdentifier());
	}

	@Override
	public List<Csar> getCsars() {
		List<Csar> list = csarDao.findAll();
		for (Csar csar : list){
			populateWithTemplate(csar);
		}
		return list;
	}

	@Override
	public Csar getCsar(String identifier) {
		Csar csar = csarDao.find(identifier);
		populateWithTemplate(csar);
		return csar;
	}
}
