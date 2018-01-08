package org.opentosca.toscana.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtils {
    public static void writeTo(InputStream csarStream, OutputStream out) throws IOException {
        byte[] data = new byte[512];
        int bytesRead = 0;
        while (bytesRead != -1) {
            bytesRead = csarStream.read(data);
            if (bytesRead != -1) {
                out.write(data, 0, bytesRead);
            }
        }
        csarStream.close();
        out.close();
    }
}
