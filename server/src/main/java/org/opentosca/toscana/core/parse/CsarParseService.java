package org.opentosca.toscana.core.parse;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.model.EffectiveModel;

/**
 Offers capability to parse the TOSCA model contained in a csar (simple profile yaml).
 */
public interface CsarParseService {

    /**
     Parses the yaml template of given csar.

     @param csar a csar instance
     @return the parsed template
     @throws InvalidCsarException if the given csar is not valid in terms of its specification or parser failed
     somehow
     */
    EffectiveModel parse(Csar csar) throws InvalidCsarException;
}
