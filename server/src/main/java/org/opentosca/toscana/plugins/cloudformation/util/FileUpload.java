package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.ArrayList;
import java.util.List;

public class FileUpload {
    private final String filePath;
    private final UploadFileType fileType;

    public enum UploadFileType {
        FROM_CSAR, UTIL, OTHER
    }

    public FileUpload(String filePath, UploadFileType fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public UploadFileType getFileType() {
        return fileType;
    }

    public static List<String> getFilePaths(List<FileUpload> fileUploadList) {
        List<String> filePaths = new ArrayList<>();
        for (FileUpload fileUpload : fileUploadList) {
            filePaths.add(fileUpload.getFilePath());
        }
        return filePaths;
    }

    public static List<FileUpload> getFileUploadByType(List<FileUpload> fileUploadList, UploadFileType... uploadFileTypes) {
        List<FileUpload> fileUploadListByType = new ArrayList<>();
        for (FileUpload fileUpload : fileUploadList) {
            for (UploadFileType uploadFileType : uploadFileTypes) {
                if (fileUpload.getFileType().equals(uploadFileType)) {
                    fileUploadListByType.add(fileUpload);
                }
            }
        }
        return fileUploadListByType;
    }
}
