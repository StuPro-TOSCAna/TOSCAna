package org.opentosca.toscana.core.util.status;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatusServiceImpl implements StatusService{
	
	@Autowired
	public CsarDao repository;
	
	@Override
	public SystemStatus getSystemStatus() {
		for (Csar csar : repository.findAll()) {
			for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
				if(entry.getValue().getState() == TransformationState.TRANSFORMING) {
					return SystemStatus.TRANSFORMING;
				} else if(entry.getValue().getState() == TransformationState.ERROR) {
					return SystemStatus.ERROR;
				}
			}
		}
		return SystemStatus.IDLE;
	}
}
