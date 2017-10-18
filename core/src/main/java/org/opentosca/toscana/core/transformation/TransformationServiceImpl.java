package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.execution.TransformationExecutionService;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.util.StatusService;
import org.opentosca.toscana.core.util.SystemStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformationServiceImpl
	implements TransformationService {

	@Autowired
	public CsarDao csarDao;
	
	@Autowired
	public TransformationExecutionService executionService;
	
    @Override
    public void createTransformation(Csar csar, Platform targetPlatform) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startTransformation(Transformation transformation) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean abortTransformation(Transformation transformation) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean deleteTransformation(Transformation transformation) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
