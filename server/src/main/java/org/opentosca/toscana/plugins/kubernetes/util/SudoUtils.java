package org.opentosca.toscana.plugins.kubernetes.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 The SudoUtils are used to find the sudo install command for a given Docker Image
 <p>
 To do this the file <code>/kubernetes/docker/sudo-commands</code> in the Resource Folder gets loaded
 */
public class SudoUtils {

    private static final Map<String, String> IMAGE_MAP = new HashMap<>();

    private static final String SUDO_COMMANDS_MAP_FILE_PATH = "/kubernetes/docker/sudo-commands";

    static {
        InputStream in = SudoUtils.class.getResourceAsStream(SUDO_COMMANDS_MAP_FILE_PATH);
        Scanner scanner = new Scanner(in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            IMAGE_MAP.put(parts[0], parts[1]);
        }
    }

    /**
     Attempts to map the given Image Tag to a install Command
     <p>
     The returned Optional is empty if mapping has failed
     */
    public static Optional<String> getSudoInstallCommand(String baseImage) {
        String imageName = baseImage.split(":")[0];
        return Optional.ofNullable(IMAGE_MAP.get(imageName));
    }
}
