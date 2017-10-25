package org.opentosca.toscana.core.dummy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

import org.opentosca.toscana.core.transformation.TransformationContext;

public class FileCreatingExceutionDummyPlugin extends ExecutionDummyPlugin {
    public FileCreatingExceutionDummyPlugin(String name, boolean failDuringExec) {
        super(name, failDuringExec);
    }

    @Override
    public void transform(TransformationContext transformation) throws Exception {
        Random rnd = new Random(123456);
        for (int i = 0; i < 5; i++) {
            String outerPath = "outer-" + i;
            for (int j = 0; j < 5; j++) {
                String innerPath = "inner-" + i;
                String path = outerPath + "/" + innerPath + ".bin";
                writeFilepath(transformation, rnd, path);
            }
        }
        for (int l = 0; l < 20; l++) {
            writeFilepath(transformation, rnd, "file-" + l + ".bin");
        }
        super.transform(transformation);
    }

    public void writeFilepath(TransformationContext transformation, Random rnd, String path) throws IOException {
        byte[] data = new byte[1024 * 10];
        rnd.nextBytes(data);
        transformation.getPluginFileAccess().write(path, new ByteArrayInputStream(data));
    }
}
