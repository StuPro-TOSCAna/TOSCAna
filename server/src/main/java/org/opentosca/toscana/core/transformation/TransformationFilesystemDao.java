package org.opentosca.toscana.core.transformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.opentosca.toscana.api.exceptions.PlatformNotFoundException;
import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.transformation.artifacts.TargetArtifact;
import org.opentosca.toscana.core.transformation.logging.Log;
import org.opentosca.toscana.core.transformation.logging.LogImpl;
import org.opentosca.toscana.core.transformation.platform.Platform;
import org.opentosca.toscana.core.transformation.platform.PlatformService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Repository
public class TransformationFilesystemDao implements TransformationDao {

    public final static String ARTIFACT_FAILED_REGEX = ".+-.+_.+_failed\\.zip";
    public final static String ARTIFACT_SUCCESSFUL_REGEX = ".+-.+_.+\\.zip";
    public final static String CONTENT_DIR = "content";

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yy_hh-mm");
    private final static String FAILED = "_failed";

    private final static Logger logger = LoggerFactory.getLogger(TransformationFilesystemDao.class);
    private final PlatformService platformService;
    private CsarDao csarDao;

    @Autowired
    public TransformationFilesystemDao(PlatformService platformService) {
        this.platformService = platformService;
    }

    @Override
    public Transformation create(Csar csar, Platform platform) throws PlatformNotFoundException {
        if (!platformService.isSupported(platform)) {
            throw new PlatformNotFoundException();
        }
        Transformation transformation = new TransformationImpl(csar, platform, getLog(csar, platform));
        delete(transformation);
        csar.getTransformations().put(platform.id, transformation);
        getContentDir(transformation).mkdirs();
        return transformation;
    }

    @Override
    public void delete(Transformation transformation) {
        File transformationDir = getRootDir(transformation);
        try {
            FileUtils.deleteDirectory(transformationDir);
            transformation.getCsar().getTransformations().remove(transformation.getPlatform().id);
            logger.info("Deleted transformation directory '{}'", transformationDir);
        } catch (IOException e) {
            logger.error("Failed to delete directory of transformation '{}'", transformation, e);
        }
    }

    @Override
    public Optional<Transformation> find(Csar csar, Platform platform) {
        Set<Transformation> transformations = readFromDisk(csar);
        return Optional.ofNullable(transformations.stream()
            .filter(transformation -> transformation.getCsar().equals(csar) && transformation.getPlatform().equals(platform))
            .findFirst().orElse(null));
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
        for (File pluginEntry : transformationFiles) {
            if (!pluginEntry.isDirectory()) {
                continue;
            }
            Optional<Platform> platform = platformService.findPlatformById(pluginEntry.getName());
            if (platform.isPresent()) {
                Log log = getLog(csar, platform.get());
                Transformation transformation = new TransformationImpl(csar, platform.get(), log);
                readTargetArtifactFromDisk(transformation);
                transformations.add(transformation);
            } else {
                try {
                    logger.warn("Found transformation '{}' for unsupported platform '{}' on disk", pluginEntry, pluginEntry.getName());
                    logger.warn("Deleting '{}'", pluginEntry);
                    FileUtils.deleteDirectory(pluginEntry);
                } catch (IOException e) {
                    logger.error("Failed to delete illegal transformation directory '{}'", pluginEntry, e);
                }
            }
        }
        return transformations;
    }

    private void readTargetArtifactFromDisk(Transformation transformation) {
        File[] files = getRootDir(transformation).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.matches(ARTIFACT_FAILED_REGEX)) {
                transformation.setState(TransformationState.ERROR);
                transformation.setTargetArtifact(new TargetArtifact(file));
                break;
            } else if (fileName.matches(ARTIFACT_SUCCESSFUL_REGEX)) {
                transformation.setState(TransformationState.DONE);
                transformation.setTargetArtifact(new TargetArtifact(file));
                break;
            }
        }
    }

    @Override
    public TargetArtifact createTargetArtifact(Transformation transformation) throws FileNotFoundException {
        String csarId = transformation.getCsar().getIdentifier();
        String platformId = transformation.getPlatform().id;
        String failed = transformation.getState() == TransformationState.ERROR ? FAILED : "";
        String filename = csarId + "-" + platformId + "_" + FORMAT.format(new Date(currentTimeMillis())) + failed + ".zip";
        File outfile = new File(getRootDir(transformation), filename);
        TargetArtifact artifact = new TargetArtifact(outfile);
        transformation.setTargetArtifact(artifact);
        return artifact;
    }

    @Override
    public File getRootDir(Transformation transformation) {
        return getRootDir(transformation.getCsar(), transformation.getPlatform());
    }

    private File getRootDir(Csar csar, Platform platform) {
        return new File(csarDao.getTransformationsDir(csar), platform.id);
    }

    @Override
    public File getContentDir(Transformation transformation) {
        return new File(getRootDir(transformation), CONTENT_DIR);
    }

    private File getContentDir(Csar csar, Platform platform) {
        return new File(getRootDir(csar, platform), CONTENT_DIR);
    }

    @Override
    public void setCsarDao(CsarDao csarDao) {
        this.csarDao = csarDao;
    }

    private Log getLog(Csar csar, Platform platform) {
        File logFile = new File(getContentDir(csar, platform), format("%s-%s.log", csar.getIdentifier(), platform.id));
        return new LogImpl(logFile);
    }
}
