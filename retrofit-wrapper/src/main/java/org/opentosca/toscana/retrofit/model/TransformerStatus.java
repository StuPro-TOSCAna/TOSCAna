package org.opentosca.toscana.retrofit.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransformerStatus {

    private String status;
    private TransformerHealth transformerHealth;
    private FileSystemHealth fileSystemHealth;

    public TransformerStatus(
        @JsonProperty("status") String status,
        @JsonProperty("transformer") TransformerHealth transformerHealth,
        @JsonProperty("diskSpace") FileSystemHealth fileSystemHealth
    ) {
        this.status = status;
        this.transformerHealth = transformerHealth;
        this.fileSystemHealth = fileSystemHealth;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("transformer")
    public TransformerHealth getTransformerHealth() {
        return transformerHealth;
    }

    @JsonProperty("diskSpace")
    public FileSystemHealth getFileSystemHealth() {
        return fileSystemHealth;
    }

    public static class TransformerHealth {
        private String status;
        private List<String> installedPlugins;
        private List<TransformationInformation> runningTransformations;
        private List<TransformationInformation> erroredTransformations;

        public TransformerHealth(
            @JsonProperty("status") String status,
            @JsonProperty("installed_plugins") List<String> installedPlugins,
            @JsonProperty("running_transformations") List<TransformationInformation> runningTransformations,
            @JsonProperty("errored_transformations") List<TransformationInformation> erroredTransformations
        ) {
            this.status = status;
            this.installedPlugins = installedPlugins;
            this.runningTransformations = runningTransformations;
            this.erroredTransformations = erroredTransformations;
        }

        @JsonProperty("status")
        public String getStatus() {
            return status;
        }

        @JsonProperty("installed_plugins")
        public List<String> getInstalledPlugins() {
            return installedPlugins;
        }

        @JsonProperty("running_transformations")
        public List<TransformationInformation> getRunningTransformations() {
            return runningTransformations;
        }

        @JsonProperty("errored_transformations")
        public List<TransformationInformation> getErroredTransformations() {
            return erroredTransformations;
        }
    }

    public static class TransformationInformation {
        private String csarName;
        private String platformName;

        public TransformationInformation(
            @JsonProperty("csar") String csarName,
            @JsonProperty("platform") String platformName
        ) {
            this.csarName = csarName;
            this.platformName = platformName;
        }

        @JsonProperty("csar")
        public String getCsarName() {
            return csarName;
        }

        @JsonProperty("platform")
        public String getPlatformName() {
            return platformName;
        }
    }

    public static class FileSystemHealth {

        private String status;
        private Long totalBytes;
        private Long freeBytes;
        private Long threshold;

        public FileSystemHealth(
            @JsonProperty("status") String status,
            @JsonProperty("total") Long totalBytes,
            @JsonProperty("free") Long freeBytes,
            @JsonProperty("threshold") Long threshold
        ) {
            this.status = status;
            this.totalBytes = totalBytes;
            this.freeBytes = freeBytes;
            this.threshold = threshold;
        }

        @JsonProperty("status")
        public String getStatus() {
            return status;
        }

        @JsonProperty("total")
        public Long getTotalBytes() {
            return totalBytes;
        }

        @JsonProperty("free")
        public Long getFreeBytes() {
            return freeBytes;
        }

        @JsonProperty("threshold")
        public Long getThreshold() {
            return threshold;
        }
    }
}
