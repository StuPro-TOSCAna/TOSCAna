package org.opentosca.toscana.core.testdata;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

public class ByteArrayUtils {
    public static byte[] getSHA256Hash(byte[] data) throws NoSuchAlgorithmException {
        // Get the sha hash of the data
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    public static byte[] generateRandomByteArray(Random rnd, int sizeKiB) {
        //Generate 10 MiB of "Random" (seeded) data
        byte[] data = new byte[(int) (Math.pow(2, 10) * sizeKiB)];
        rnd.nextBytes(data);
        return data;
    }

    public static void assertHashesEqual(byte[] hash, byte[] hashUpload) {
        assertSame(hash.length, hashUpload.length);
        assertArrayEquals(hash, hashUpload);
    }
}
