package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.ArrayList;
import java.util.List;

/**
 Holds information about a file that needs to be uploaded.
 */
public class FileUpload {

    private final String filePath;
    /**
     The type this FileUpload has.
     <br>
     Different types get treated differently.
     */
    private final UploadFileType fileType;

    /**
     The different types a {@link FileUpload} can have.
     */
    public enum UploadFileType {
        /**
         All files that need to be uploaded for any AWS Resource and origin from the transformation's CSAR have this type.
         */
        FROM_CSAR,
        /**
         All files that are not from the CSAR and are utility files (for example a create script that does not come from
         the csar but from the plugin) have this type.
         */
        UTIL,
        /**
         All files that are not of the other two types have this type.
         */
        OTHER
    }

    /**
     Creates a FileUpload using the {@code filePath} and the {@code fileType}.

     @param filePath the file path where the file is stored
     @param fileType the {@link UploadFileType} this file should have
     */
    public FileUpload(String filePath, UploadFileType fileType) {
        this.filePath = filePath;
        this.fileType = fileType;
    }

    /**
     Gets the {@link #filePath}.

     @return the {@link #filePath}
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     Gets the {@link #fileType}.

     @return the {@link #fileType}
     */
    public UploadFileType getFileType() {
        return fileType;
    }

    /**
     Gets the {@link #filePath} from all files in the {@code fileUploadList}.

     @param fileUploadList the {@link List} of FileUploads of which to return the file paths from
     @return returns a {@link List} of file paths
     */
    public static List<String> getFilePaths(List<FileUpload> fileUploadList) {
        List<String> filePaths = new ArrayList<>();
        for (FileUpload fileUpload : fileUploadList) {
            filePaths.add(fileUpload.getFilePath());
        }
        return filePaths;
    }

    /**
     Gets the {@link #filePath} from all files in the {@code fileUploadList} having one of the {@code uploadFileTypes}.

     @param fileUploadList  the {@link List} of FileUploads of which to return the file paths from
     @param uploadFileTypes one or multiple {@link UploadFileType}s that are allowed
     @return returns a {@link List} of file paths
     */
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
