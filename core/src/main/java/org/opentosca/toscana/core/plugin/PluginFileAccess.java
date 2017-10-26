package org.opentosca.toscana.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.transformation.Transformation;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class PluginFileAccess {

    private final Logger logger;
    private final File sourceDir;
    private final File targetDir;

    public PluginFileAccess(Transformation transformation, File sourceDir, File targetDir) {
        logger = transformation.getLog().getLogger(getClass());
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    /**
     * Copies given file or directory from the csar content directory to the transformation directory If given path
     * specifies a directory, this directory's content gets copied aswell
     *
     * @param relativePath the path to the source file relative to the csar's content directory
     * @throws FileNotFoundException if no file or directory was found for given path
     * @throws IOException           if an error occurred while copying
     */
    public void copy(String relativePath) throws FileNotFoundException, IOException {
        File source = new File(sourceDir, relativePath);
        File target = new File(targetDir, relativePath);
        if (!source.exists()) {
            logger.error("Failed to copy '{}': file not found", sourceDir);
            throw new FileNotFoundException();
        }
        try {
            if (source.isDirectory()) {
                FileUtils.copyDirectory(source, target);
                logger.info("Copied directory '{}' and its content to '{}'", sourceDir, targetDir);
            } else if (source.isFile()) {
                FileUtils.copyFile(source, target);
                logger.info("Copied file '{}' to '{}'", sourceDir, targetDir);
            }
        } catch (IOException e) {
            logger.error("Failed to copy from '{}' to '{}'", source, target, e);
            throw e;
        }
    }

    /**
     * Writes given InputStream to given path If necessary, creates missing subdirectories.
     *
     * @param relativePath path to the target file, relative to the transformations root dir
     * @param inputStream  stream which will get written to file. Afterwards, this stream will be closed
     */
    public void write(String relativePath, InputStream inputStream) throws IOException {
        File target = new File(targetDir, relativePath);
        try {
            FileUtils.copyInputStreamToFile(inputStream, target);
            logger.info("Written stream to new file '{}'", target);
        } catch (IOException e) {
            logger.error("Failed to write stream to '{}'", target);
            throw e;
        } finally {
            inputStream.close();
        }
    }

    /**
     * Reads the content of a file in the csar content diretory denoted by given path.
     *
     * @param relativePath path to a file contained in the csar content directory, relative said directory
     * @return InputStream of given file
     * @throws FileNotFoundException if file denoted by given relativePath does not exist or is a directory
     */
    public InputStream read(String relativePath) throws FileNotFoundException {
        File source = new File(sourceDir, relativePath);
        return new FileInputStream(source);
    }
}
