package org.opentosca.toscana.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtility {
    private final static Logger logger = LoggerFactory.getLogger(ZipUtility.class.getName());

    /**
     Size of the buffer to read/access data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     Extracts a ZipInputStream specified by the zipIn to a directory specified by destDirectory (will be created if
     does not exists)

     @param zipIn         ZipInputStream of zip archive
     @param destDirectory target directory for unzipping
     @return true if successfully extracted, false if given zip was empty or not a zip at all
     */
    public static boolean unzip(ZipInputStream zipIn, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipEntry entry = zipIn.getNextEntry();
        if (entry == null) {
            return false;
        }
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                logger.trace("Creating directory: {}", filePath);
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
        return true;
    }

    /**
     Extracts a zip entry (file entry)
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        logger.trace("Extracting file: {}", filePath);
        //Create parent directories if they don't exist
        File parentPath = new File(filePath).getParentFile();
        if (!parentPath.exists() && !parentPath.mkdirs()) {
            throw new IOException("Could not create directory " + parentPath.getAbsolutePath());
        }
        //Unzip file
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    /**
     Compresses given directory recursively to given output stream
     */
    public static void compressDirectory(File directory, OutputStream output) throws IOException {
        logger.debug("Compressing directory {}", directory.getAbsolutePath());
        ZipOutputStream zipOut = new ZipOutputStream(output);

        LinkedList<File> fileStack = new LinkedList<>();
        fileStack.push(directory);

        while (!fileStack.isEmpty()) {
            File f = fileStack.pop();
            if (f.isDirectory()) {
                for (File file : f.listFiles()) {
                    fileStack.push(file);
                }
            } else {
                FileInputStream in = new FileInputStream(f);
                //Get the relative path
                String relative = directory.toPath().relativize(f.toPath()).toString();
                logger.trace("Compressing {}", relative);

                //create the zip entry
                ZipEntry entry = new ZipEntry(relative);
                zipOut.putNextEntry(entry);

                //Write the data
                byte[] buffer = new byte[BUFFER_SIZE];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    zipOut.write(buffer, 0, read);
                }

                //Close the zip entry
                zipOut.closeEntry();
                zipOut.flush();

                //close the input stream
                in.close();
            }
        }
        //close zip output
        zipOut.close();
        logger.debug("Closing stream");
    }

}
