package org.opentosca.toscana.model;

import java.io.File;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.parse.InvalidCsarException;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.logging.Log;

import org.springframework.stereotype.Service;

@Service
public class EffectiveModelFactory {

    public EffectiveModel create(Csar csar) throws InvalidCsarException {
        return new EffectiveModel(csar);
    }

    public EffectiveModel create(File template, Log log) {
        return new EffectiveModel(template, log);
    }
}
