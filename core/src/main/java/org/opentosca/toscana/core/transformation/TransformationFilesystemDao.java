package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransformationFilesystemDao implements TransformationDao {

    private final static Logger logger = LoggerFactory.getLogger(TransformationFilesystemDao.class);
    private CsarDao csarDao;
    private PlatformService platformService;

    @Autowired
    public TransformationFilesystemDao(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Override
    public Transformation create(Csar csar, Platform platform) {
        Transformation transformation = new TransformationImpl(csar, platform);
        delete(transformation);
        csar.getTransformations().put(platform.id, transformation);
        getRootDir(transformation).mkdir();
        return transformation;
    }

    @Override
    public void delete(Transformation transformation) {
        File transformationDir = getRootDir(transformation);
        try {
            FileUtils.deleteDirectory(transformationDir);
            transformation.getCsar().getTransformations().remove(transformation.getPlatform().id);
            logger.info("deleted transformation directory '{}'", transformationDir);
        } catch (IOException e) {
            logger.error("failed to delete directory of transformation '{}'", transformation, e);
        }
    }

    @Override
    public Transformation find(Csar csar, Platform platform) {
        Set<Transformation> transformations = readFromDisk(csar);
        return transformations.stream()
            .filter(transformation -> transformation.getCsar().equals(csar) && transformation.getPlatform().equals(platform))
            .findFirst().orElse(null);
    }

    @Override
    public List<Transformation> find(Csar csar) {
        Set<Transformation> transformations = readFromDisk(csar);
        return transformations.stream()
            .filter(transformation -> transformation.getCsar().equals(csar))
            .collect(Collectors.toList());
    }

    private Set<Transformation> readFromDisk(Csar csar) {
        File[] transformationFiles = csarDao.getTransformationsDir(csar).listFiles();
        Set<Transformation> transformations = new HashSet<>();
        for (File transformationFile : transformationFiles) {
            Platform platform = getPlatform(transformationFile);
            Transformation transformation = new TransformationImpl(csar, platform);
            // TODO set transformation state
            transformations.add(transformation);
        }
        return transformations;
    }

    private Platform getPlatform(File dir) {
        if (dir.isDirectory()) {
            return platformService.findById(dir.getName());
        } else {
            return null;
        }
    }

    @Override
    public File getRootDir(Transformation transformation) {
        return new File(csarDao.getTransformationsDir(transformation.getCsar()), transformation.getPlatform().id);
    }

    public void setCsarDao(CsarDao csarDao) {
        this.csarDao = csarDao;
    }
}
