package org.opentosca.toscana.core.util.status;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatusServiceImpl implements StatusService {

    private final CsarDao repository;

    @Autowired
    public StatusServiceImpl(CsarDao repository) {
        this.repository = repository;
    }

    @Override
    public SystemStatus getSystemStatus() {
        SystemStatus status = SystemStatus.IDLE;
        for (Csar csar : repository.findAll()) {
            for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
                if (entry.getValue().getState() == TransformationState.TRANSFORMING) {
                    status = SystemStatus.TRANSFORMING;
                } else if (entry.getValue().getState() == TransformationState.ERROR) {
                    return SystemStatus.ERROR;
                }
            }
        }
        return status;
    }
}
