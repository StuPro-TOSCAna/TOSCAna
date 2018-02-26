package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.ArrayList;
import java.util.List;

public class FileToBeUploaded {
    private final String filePath;
    private final UploadFileType fileType;

    public enum UploadFileType {
        FROM_CSAR, UTIL, OTHER
    }

    public FileToBeUploaded(String filePath, UploadFileType fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public UploadFileType getFileType() {
        return fileType;
    }
    
    public static List<String> getFilePaths(List<FileToBeUploaded> filesToBeUploaded) {
        List<String> filePaths = new ArrayList<>();
        for (FileToBeUploaded fileToBeUploaded : filesToBeUploaded) {
            filePaths.add(fileToBeUploaded.getFilePath());
        }
        return filePaths;
    }

    public static List<FileToBeUploaded> getFileToBeUploadedByType(List<FileToBeUploaded> filesToBeUploaded, UploadFileType... uploadFileTypes) {
        List<FileToBeUploaded> filesToBeUploadedByType = new ArrayList<>();
        for (FileToBeUploaded fileToBeUploaded : filesToBeUploaded) {
            for (UploadFileType uploadFileType : uploadFileTypes) {
                if (fileToBeUploaded.getFileType().equals(uploadFileType)) {
                    filesToBeUploadedByType.add(fileToBeUploaded);
                }
            }
        }
        return filesToBeUploadedByType;
    }
}
