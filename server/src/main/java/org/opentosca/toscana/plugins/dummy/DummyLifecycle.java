package org.opentosca.toscana.plugins.dummy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

import org.opentosca.toscana.core.transformation.TransformationContext;
import org.opentosca.toscana.plugins.lifecycle.AbstractLifecycle;

public class DummyLifecycle extends AbstractLifecycle {

    private static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789".toCharArray();
    private boolean fail;

    public DummyLifecycle(TransformationContext context, boolean fail) throws IOException {
        super(context);
        this.fail = fail;
    }

    private void writeRandomData(int len, String name) throws IOException {
        logger.info("Writing file {}, Length: {}", name, len);
        BufferedWriter out = context.getPluginFileAccess().access(name);
        Random rnd = new Random();
        for (int i = 0; i < len; i++) {
            out.append(chars[rnd.nextInt(chars.length)]);
        }
        out.close();
    }

    @Override
    public boolean checkModel() {
        logger.info("Checking model");
        return true;
    }

    @Override
    public void prepare() {
        genRandomFiles("-prep.txt");
    }

    @Override
    public void transform() {
        genRandomFiles("-transform-a.txt");
        if (fail) {
            throw new NullPointerException("Transformation Failed!");
        }
        String suffix = "-transform-b.txt";
        genRandomFiles(suffix);
    }

    private void genRandomFiles(String suffix) {
        for (int i = 0; i < 50; i++) {
            try {
                writeRandomData(1000 + i * 1000, i + suffix);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cleanup() {

    }
}
