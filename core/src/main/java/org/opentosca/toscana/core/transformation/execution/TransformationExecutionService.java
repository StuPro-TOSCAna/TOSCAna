package org.opentosca.toscana.core.transformation.execution;

import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.util.StatusService;
import org.opentosca.toscana.core.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;

@Service
public class TransformationExecutionService implements StatusService{

	public Logger log = LoggerFactory.getLogger(getClass());
	
	private SystemStatus systemStatus = SystemStatus.IDLE;
	private Deque<Transformation> queuedTransformations;
	
	public TransformationExecutionService() {
		this.queuedTransformations = new ArrayDeque<>();
	}
	
	public void scheduleTransformation(Transformation transformation) {
		this.queuedTransformations.add(transformation);
	}
	
	@Override
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}
	
	@Scheduled(fixedRate = 10000)
	public void exceuteTransformations() {
		
	}
}
