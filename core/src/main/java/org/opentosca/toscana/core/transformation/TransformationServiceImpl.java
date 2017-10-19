package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.execution.ExecutionTask;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class TransformationServiceImpl
	implements TransformationService {

	@Autowired
	public CsarDao csarDao;

	public Logger log = LoggerFactory.getLogger(getClass());

	private SystemStatus systemStatus = SystemStatus.IDLE;
	private Deque<Transformation> queuedTransformations = new ConcurrentLinkedDeque<>();
	private List<ExecutionTask> currentTasks = Collections.synchronizedList(new ArrayList<>());
	
	private Executor executor = Executors.newSingleThreadExecutor();
	
	@Override
	public void createTransformation(Csar csar, Platform targetPlatform) {
		Transformation transformation = new TransformationImpl(csar, targetPlatform);
		
		//TODO Implement check to find out if inputs are needed
		
		csar.getTransformations().put(targetPlatform.id, transformation);
	}

	@Override
	public boolean startTransformation(Transformation transformation) {
		return transformation.getState() == TransformationState.CREATED
			&& transformation.getState() == TransformationState.INPUT_REQUIRED 
			&& queuedTransformations.add(transformation);
	}
	
	@Scheduled(fixedDelay = 5000)
	public void scheduleTransformations() {
		if(!queuedTransformations.isEmpty()) {
			Transformation t = queuedTransformations.poll();
			
		}
	}

	@Override
	public boolean abortTransformation(Transformation transformation) {
		return false;
	}

	@Override
	public boolean deleteTransformation(Transformation transformation) {
		return false;
	}

	@Override
	public SystemStatus getSystemStatus() {
		return systemStatus;
	}

	@Override
	public synchronized void setSystemStatus(SystemStatus status) {
		this.systemStatus = status;
	}
}
