package org.opentosca.toscana.core.parse;

import org.opentosca.toscana.core.csar.Csar;

import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;

/**
 Offers capability to parse the TOSCA model contained in a csar (simple profile yaml).
 */
public interface CsarParseService {

    /**
     Parses the yaml template of given csar.

     @param csar a csar instance
     @return the parsed template
     @throws InvalidCsarException if the given csar is not valid in terms of its specification or parser failed
     sommehow
     */
    public TServiceTemplate parse(Csar csar) throws InvalidCsarException;
}
