package org.opentosca.toscana.core.transformation;

import org.apache.commons.io.FileUtils;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransformationFilesystemDao implements TransformationDao {

    private final static Logger logger = LoggerFactory.getLogger(TransformationFilesystemDao.class);
    private final CsarDao csarDao;
    private PlatformService platformService;

    /**
     * contains all transformations. filesystem is read once during construction
     */
    private final List<Transformation> transformationList = new ArrayList<>();

    @Autowired
    public TransformationFilesystemDao(CsarDao csarDao, PlatformService service) {
        this.csarDao = csarDao;
        this.platformService = service;
    }

    @Override
    public Transformation create(Csar csar, Platform platform) {
        Transformation transformation = new TransformationImpl(csar, platform);
        delete(transformation);
        getRootDir(transformation).mkdir();
        return transformation;
    }


    @Override
    public void delete(Transformation transformation) {
        File transformationDir = getRootDir(transformation);
        try {
            FileUtils.deleteDirectory(transformationDir);
            logger.info("deleted transformation directory '{}'", transformationDir);
        } catch (IOException e) {
            logger.error("failed to delete directory of transformation '{}'", transformation, e);
        }

    }

    @Override
    public Transformation find(Csar csar, Platform platform) {
        readFromDisk();
        return transformationList.stream()
            .filter(transformation -> transformation.getCsar().equals(csar) && transformation.getPlatform().equals(platform))
            .findFirst().orElse(null);
    }

    @Override
    public List<Transformation> find(Csar csar) {
        readFromDisk();
        return transformationList.stream()
            .filter(transformation -> transformation.getCsar().equals(csar))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transformation> findAll() {
        readFromDisk();
        return transformationList;
    }

    private void readFromDisk() {
        for (Csar csar : csarDao.findAll()) {
            File[] transformationFiles = csarDao.getTransformationsDir(csar).listFiles();
            for (File transformationFile : transformationFiles) {
                Platform platform = getPlatform(transformationFile);
                Transformation transformation = new TransformationImpl(csar, platform);
                // TODO set transformation state
                transformationList.add(transformation);
            }
        }
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
}
