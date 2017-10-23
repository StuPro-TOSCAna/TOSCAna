package org.opentosca.toscana.core.transformation;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.transformation.platform.Platform;

import java.util.List;

public class TransformationFilesystemDao implements TransformationDao{
    
    @Override
    public Transformation create(Csar csar, Platform platform) {
        return null;
    }

    @Override
    public void delete(Transformation transformation) {

    }

    @Override
    public Transformation find(Csar csar, Platform platform) {
        return null;
    }

    @Override
    public Transformation find(Csar csar) {
        return null;
    }

    @Override
    public List<Transformation> findAll() {
        return null;
    }
}
