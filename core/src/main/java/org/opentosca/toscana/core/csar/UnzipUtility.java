package org.opentosca.toscana.core.csar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


class UnzipUtility {

	private final static Logger logger = LoggerFactory.getLogger(UnzipUtility.class.getName());
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extracts a ZipInputStream specified by the zipIn to a directory specified by
	 * destDirectory (will be created if does not exists)
	 *
	 * @param zipIn         ZipInputStream of zip archive
	 * @param destDirectory target directory for unzipping
	 * @throws IOException
	 */
	static void unzip(ZipInputStream zipIn, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 *
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		//Create parent directories if they dont exist
		File parentPath = new File(filePath).getParentFile();
		if (!parentPath.exists() && !parentPath.mkdirs()) {
			throw new IOException("Could not create directory " + parentPath.getAbsolutePath());
		}
		//Unzip file
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

}
