package org.opentosca.toscana.core.plugin;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.opentosca.toscana.core.transformation.logging.Log;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class PluginFileAccess {

    private final Logger logger;
    private final File sourceDir;
    private final File targetDir;

    public PluginFileAccess(File sourceDir, File targetDir, Log log) {
        this.logger = log.getLogger(getClass());
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    /**
     Copies given file or directory (recursively) from the csar content directory to the transformation directory.
     After the successful call the copied files will reside in given path (relative to the transformation content dir)

     @param relativePath path to the source file relative to the csar content directory
     @throws IOException if an error occurred while copying
     */
    public void copy(String relativePath) throws IOException {
        copy(relativePath, relativePath);
    }

    /**
     Copies given file or directory (recursively) located at given relativeSourcePath to the given relativeTargetPath.
     <p>
     Note: Will create new directories if necessary.

     @param relativeSourcePath path to the source file relative to the csar content directory
     @param relativeTargetPath target location for the copy operation, relative to the transformation content dir
     @throws IOException if an error occured while copying
     */
    public void copy(String relativeSourcePath, String relativeTargetPath) throws IOException {
        File source = new File(sourceDir, relativeSourcePath);
        File target = new File(targetDir, relativeTargetPath);
        if (!source.exists()) {
            logger.error("Failed to copy '{}': file not found", sourceDir);
            throw new FileNotFoundException();
        }
        try {
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, target);
                logger.info("Copied directory '{}' and its content to '{}'", source, target);
            } else if (source.isFile()) {
                FileUtils.copyFile(source, target);
                logger.info("Copied file '{}' to '{}'", source, target);
            }
        } catch (IOException e) {
            logger.error("Failed to copy from '{}' to '{}'", source, target, e);
            throw e;
        }
    }

    /**
     Returns a BufferedWriter which can write to given path.
     <p>
     If necessary, creates missing subdirectories.
     <p>
     Note: Close returned BufferWriter after usage.
     <p>
     Tip (single-line usage): fileAccess.access("myPath").append("message").close()

     @param relativePath path to the target file, relative to the transformations root dir
     @return BufferedWriter which writes to target file.
     @throws FileNotFoundException if given relativePath points to a directory
     */
    public BufferedLineWriter access(String relativePath) throws IOException {
        File target = new File(targetDir, relativePath);
        target.getParentFile().mkdirs();
        try {
            return new BufferedLineWriter(new FileWriter(target, true));
        } catch (FileNotFoundException e) {
            logger.error("Failed to create OutputStream for file '{}'", target);
            throw e;
        }
    }

    /**
     Returns a BufferedOutputStream which can write to given path.
     <p>
     This method is the ByteStream Counterpart to the <code>access</code> method.
     <p>
     Please do not use this method to write Character encoded files,
     the regular <code>access</code> method is intended to handle Character based Streams.
     <p>
     If necessary, creates missing subdirectories.
     <p>
     Note: Close returned BufferedOutputStream after usage.

     @param relativePath path to the target file, relative to the transformations root dir
     @return BufferedOutputStream which writes to target file.
     @throws FileNotFoundException if given relativePath points to a directory
     */
    public BufferedOutputStream accessAsOutputStream(String relativePath) throws IOException {
        File target = new File(targetDir, relativePath);
        target.getParentFile().mkdirs();
        try {
            return new BufferedOutputStream(new FileOutputStream(target));
        } catch (FileNotFoundException e) {
            logger.error("Failed to create OutputStream for file '{}'", target);
            throw e;
        }
    }

    /**
     Returns the content of a file in the csar content directory denoted by given path.

     @param relativePath path to a file contained in the csar content directory, relative said directory
     @return content of given file
     @throws IOException if file denoted by given relativePath does not exist or is a directory
     */
    public String read(String relativePath) throws IOException {
        File source = new File(sourceDir, relativePath);
        try {
            return FileUtils.readFileToString(source);
        } catch (IOException e) {
            logger.error("Failed to read content from file '{}'", source);
            throw e;
        }
    }

    /**
     @param relativePath a path (relative to the transformation content directory) to a file or directory.
     @return the absolute path for given relativePath.
     @throws FileNotFoundException if no file is found for given relativePath
     */
    public String getAbsolutePath(String relativePath) throws FileNotFoundException {
        File targetFile = new File(targetDir, relativePath);
        if (targetFile.exists()) {
            return targetFile.getAbsolutePath();
        } else {
            throw new FileNotFoundException(String.format("File '%s' not found", targetFile));
        }
    }

    /**
     Deletes file or directory (recusively) denoted by given path. If target does not exists, does nothing.

     @param relativePath relative (to the transformation content directory) path to a file or directory.
     */
    public void delete(String relativePath) {
        File file = new File(targetDir, relativePath);
        FileUtils.deleteQuietly(file);
    }

    /**
     Creates folders recursively in the transformation content directory. Does nothing if folder already exists.

     @param relativePath relative (to the transformation content directory) path to a file or directory.
     @return the created folder.
     */
    public void createDirectories(String relativePath) {
        File targetFolder = new File(targetDir, relativePath);
        targetFolder.mkdirs();
    }

    public static class BufferedLineWriter extends BufferedWriter {

        BufferedLineWriter(FileWriter fw) {
            super(fw);
        }

        /**
         Same as {@link #append}, but terminates line with a linebreak.
         */
        public Writer appendln(String string) throws IOException {
            append(string + "\n");
            return this;
        }
    }
}
