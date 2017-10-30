package org.opentosca.toscana.core.util.health;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opentosca.toscana.core.csar.Csar;
import org.opentosca.toscana.core.csar.CsarDao;
import org.opentosca.toscana.core.plugin.PluginService;
import org.opentosca.toscana.core.transformation.Transformation;
import org.opentosca.toscana.core.transformation.TransformationState;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class TransformerHealthIndicator extends AbstractHealthIndicator {

    private static final String INSTALLED_PLUGINS_KEY = "installed_plugins";
    private static final String RUNNING_TRANSFORMATIONS_KEY = "running_transformations";
    private static final String ERRORED_TRANSFORMATIONS_KEY = "errored_transformations";

    private CsarDao repository;
    private PluginService pluginService;

    @Autowired
    public TransformerHealthIndicator(
        CsarDao repository,
        PluginService pluginService
    ) {
        this.repository = repository;
        this.pluginService = pluginService;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        List<String> loadedPlugins = new ArrayList<>();
        pluginService.getSupportedPlatforms().forEach(e -> loadedPlugins.add(e.id));
        builder.withDetail(INSTALLED_PLUGINS_KEY, loadedPlugins);
        builder.withDetail(RUNNING_TRANSFORMATIONS_KEY,
            getTransformationInfoForState(TransformationState.TRANSFORMING));
        builder.withDetail(ERRORED_TRANSFORMATIONS_KEY,
            getTransformationInfoForState(TransformationState.ERROR));
        builder.up().build();
    }

    private List<TransformationInformation> getTransformationInfoForState(TransformationState state) {
        List<TransformationInformation> transformations = new ArrayList<>();
        for (Csar csar : repository.findAll()) {
            for (Map.Entry<String, Transformation> entry : csar.getTransformations().entrySet()) {
                if (entry.getValue().getState() == state) {
                    transformations.add(new TransformationInformation(entry.getValue()));
                }
            }
        }
        return transformations;
    }

    private static class TransformationInformation {
        private String csarIdentifier;
        private String platformIdentifier;

        TransformationInformation(Transformation transformation) {
            this(transformation.getCsar().getIdentifier(), transformation.getPlatform().id);
        }

        TransformationInformation(
            @JsonProperty("csar") String csarIdentifier,
            @JsonProperty("platform") String platformIdentifier
        ) {
            this.csarIdentifier = csarIdentifier;
            this.platformIdentifier = platformIdentifier;
        }

        @JsonProperty("csar")
        public String getCsarIdentifier() {
            return csarIdentifier;
        }

        @JsonProperty("platform")
        public String getPlatformIdentifier() {
            return platformIdentifier;
        }
    }
}
