package org.opentosca.toscana.core.util.status;

/**
 A class that implements this class is able to provide the current status of the transformer
 */
public interface StatusService {
    /**
     @return the current status of the transformer (never Null!)
     */
    SystemStatus getSystemStatus();
}
