package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class SudoUtils {

    private static final Map<String, String> IMAGE_MAP = new HashMap<>();

    static {
        InputStream in = SudoUtils.class.getResourceAsStream("/kubernetes/docker/sudo-commands");
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            IMAGE_MAP.put(parts[0], parts[1]);
        }
    }

    public static Optional<String> getSudoInstallCommand(String baseImage) {
        String imageName = baseImage.split(":")[0];
        return Optional.ofNullable(IMAGE_MAP.get(imageName));
    }
}
