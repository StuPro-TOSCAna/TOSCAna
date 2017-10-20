package org.opentosca.toscana.core.parse;

import org.junit.Test;
import org.opentosca.toscana.core.BaseSpringTest;
import org.opentosca.toscana.core.TestData;
import org.opentosca.toscana.core.csar.Csar;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class CsarParserImplTest extends BaseSpringTest {
	
	@Autowired
	CsarParser csarParser;
	@Autowired
	TestData testData;
	
	@Test
	public void parse() throws Exception {
		Csar csar = testData.getCsar(TestData.CSAR_YAML_VALID_SIMPLETASK);
		// csarParser.parse(csar);
		//assertTrue(false);
		// TODO write test
		
		
	}

}
