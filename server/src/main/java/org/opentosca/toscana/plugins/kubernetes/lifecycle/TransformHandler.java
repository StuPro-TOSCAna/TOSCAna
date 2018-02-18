package org.opentosca.toscana.plugins.kubernetes.lifecycle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.opentosca.toscana.plugins.kubernetes.ResourceFileCreator;
import org.opentosca.toscana.plugins.kubernetes.docker.image.ExportingImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.image.ImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.image.PushingImageBuilder;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.BaseImageMapper;
import org.opentosca.toscana.plugins.kubernetes.docker.util.DockerRegistryCredentials;
import org.opentosca.toscana.plugins.kubernetes.model.transform.RelationshipGraph;
import org.opentosca.toscana.plugins.kubernetes.util.NodeStack;
import org.opentosca.toscana.plugins.kubernetes.util.ScriptHelper;
import org.opentosca.toscana.plugins.util.TransformationFailureException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TransformHandler extends LifecycleHandler {
    
    private final BaseImageMapper baseImageMapper;
    private final boolean pushToRegistry;    
    private final Map<NodeStack, ImageBuilder> imageBuilders = new HashMap<>();

    TransformHandler(
        KubernetesLifecycle lifecycle,
        BaseImageMapper mapper,
        boolean pushToRegisty
    ) {
        super(lifecycle);
        this.baseImageMapper = mapper;
        this.pushToRegistry = pushToRegisty;
    }

    public void transform() {
        logger.info("Transforming...");
        createDockerfiles();
        buildDockerImages();
        createKubernetesResources();
        writeHelperScripts();
    }

    /**
     Builds all Dockerimages of the stack and exports the images as a tar archive.
     */
    private void buildDockerImages() {
        instantiateImageBuilders();

        logger.info("Building Docker images");
        imageBuilders.forEach((stack, builder) -> {
            logger.info("Building {}", stack);
            try {
                builder.buildImage();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new TransformationFailureException(
                    "Transformation Failed, while building a docker image for " + stack,
                    ex
                );
            }
        });

        storeDockerImages();
    }

    private void storeDockerImages() {
        logger.info("Storing Docker images");
        imageBuilders.forEach((stack, builder) -> {
            logger.info("Storing {}", stack);
            try {
                builder.storeImage();
                stack.setDockerImageTag(builder.getTag());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new TransformationFailureException(
                    "Transformation Failed, while storing a docker image for " + stack,
                    ex
                );
            }
        });
    }

    private void instantiateImageBuilders() {
        logger.debug("Instantiating Docker Image Builders");
        lifecycle.stacks.stream().filter(NodeStack::stackRequiresBuilding).forEach(e -> {
            ImageBuilder builder;
            if (!pushToRegistry) {
                builder = new ExportingImageBuilder(
                    "output/docker/" + e.getStackName() + ".tar.gz",
                    e.getStackName(),
                    e.getDockerfilePath().get(),
                    lifecycle.getContext()
                );
            } else {
                builder = new PushingImageBuilder(
                    DockerRegistryCredentials.fromContext(lifecycle.getContext()),
                    e.getStackName(),
                    e.getDockerfilePath().get(),
                    lifecycle.getContext()
                );
            }

            imageBuilders.put(e, builder);
        });
    }

    /**
     Creates the Dockerfiles (that means the dockerfile and all its dependesies get written to disk)
     */
    private void createDockerfiles() {
        RelationshipGraph connectionGraph = new RelationshipGraph(lifecycle.stacks);
        lifecycle.stacks.forEach(e -> {
            logger.info("Creating Dockerfile for {}", e);
            try {
                e.buildToDockerfile(connectionGraph, lifecycle.getContext(), baseImageMapper);
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new TransformationFailureException("Transformation Failed", ex);
            }
        });
    }

    /**
     Writes the kuberenetes Resources of the nodes into the output/kubernetes
     */
    private void createKubernetesResources() {
        logger.info("Creating Kubernetes Resource Descriptions");

        ResourceFileCreator creator = new ResourceFileCreator(lifecycle.pods);

        StringBuilder complete = new StringBuilder();
        try {
            creator.create().forEach((k, resource) -> {
                complete.append(resource);
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        try {
            lifecycle.getContext().getPluginFileAccess().access("output/kubernetes-resources/complete.yml").append(complete.toString()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeHelperScripts() {
        logger.info("Writing Helper Scripts");
        try {
            ScriptHelper.copyScripts(pushToRegistry, lifecycle.getContext().getPluginFileAccess());
        } catch (IOException e) {
            throw new TransformationFailureException("Copying of Helper scripts failed", e);
        }
    }

    public void cleanup() {
        removeDockerImages();
    }

    private void removeDockerImages() {
        logger.info("Removing built Docker Images");

        for (ImageBuilder builder : imageBuilders.values()) {
            try {
                builder.cleanup();
            } catch (Exception e) {
                logger.error("Docker Image Cleanup failed!", e);
                throw new TransformationFailureException(
                    "Transformaton Cleanup failed, while cleaning up Docker Images",
                    e
                );
            }
        }
    }
}
