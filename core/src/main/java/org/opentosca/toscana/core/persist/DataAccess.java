package org.opentosca.toscana.core.persist;

import org.opentosca.toscana.core.model.CsarImpl;

import java.util.List;

public class DataAccess {

    /**
     * Returns the list of all currently stored applications.
     * If no application is stored, returns an empty list.
     */
    public List<CsarImpl> getApplications(){
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the (by the application model) used storage space in MB.
     */
    public long getUsedSpace(){
       // TODO
       throw new UnsupportedOperationException();
    }

    /**
     * Returns the available storage space in MB.
     */
    public long getAvailableSpace(){
        // TODO
        throw new UnsupportedOperationException();
    }
}
