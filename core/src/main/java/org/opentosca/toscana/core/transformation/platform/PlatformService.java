package org.opentosca.toscana.core.transformation.platform;

import java.util.Optional;
import java.util.Set;

/**
 This interface is used by the Platform Controller (REST API) to get a list of supported platforms
 */
public interface PlatformService {
    /**
     @return Returns a list of all supported Platforms
     */
    Set<Platform> getSupportedPlatforms();

    /**
     Finds a specific platform by its identifier

     @param id the id to look for
     @return the resulting platform object wrapped in an optional - is empty if no match was found
     */
    Optional<Platform> findPlatformById(String id);

    boolean isSupported(Platform platform);
}
