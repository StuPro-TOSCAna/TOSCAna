package org.opentosca.toscana.core.plugin;

import org.apache.commons.io.FileUtils;
import org.opentosca.toscana.core.transformation.Transformation;
import org.slf4j.Logger;

import java.io.*;

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
     * Returns a BufferedWriter which can write to given path
     * <p>
     * If necessary, creates missing subdirectories. <br>
     * <p>
     * Note: Close returned BufferWriter after usage.
     *
     * @param relativePath path to the target file, relative to the transformations root dir
     * @return BufferedWriter which writes to target file.
     * @throws FileNotFoundException if given relativePath points to a directory
     */
    public BufferedWriter access(String relativePath) throws IOException {
        File target = new File(targetDir, relativePath);
        target.getParentFile().mkdirs();
        try {
            return new BufferedWriter(new FileWriter(target));
        } catch (FileNotFoundException e) {
            logger.error("Failed to create OutputStream for file '{}'", target);
            throw e;
        }
    }

    /**
     * Returns the content of a file in the csar content diretory denoted by given path.
     *
     * @param relativePath path to a file contained in the csar content directory, relative said directory
     * @return content of given file
     * @throws IOException if file denoted by given relativePath does not exist or is a directory
     */
    public String read(String relativePath) throws IOException {
        File source = new File(sourceDir, relativePath);
        try {
            String content = FileUtils.readFileToString(source);
            return content;
        } catch (IOException e) {
            logger.error("Failed to read content from file '{}'", source);
            throw e;
        }
    }
}
