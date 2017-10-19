package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.util.SystemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class TransformationServiceImpl
	implements TransformationService {

	@Autowired
	public CsarDao csarDao;

	public Logger log = LoggerFactory.getLogger(getClass());

	private SystemStatus systemStatus = SystemStatus.IDLE;
	private Deque<Transformation> queuedTransformations;

	public TransformationServiceImpl() {
		queuedTransformations = new ConcurrentLinkedDeque<>();
	}

	@Override
	public void createTransformation(Csar csar, Platform targetPlatform) {
		Transformation transformation = new TransformationImpl(csar, targetPlatform);

		csar.getTransformations().put(targetPlatform.id, transformation);
	}

	@Override
	public boolean startTransformation(Transformation transformation) {
		return false;
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
}
