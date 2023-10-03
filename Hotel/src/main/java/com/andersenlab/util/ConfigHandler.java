package com.andersenlab.util;

import com.andersenlab.config.ConfigData;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHandler {
    private static final String DEFAULT_PATH = "src/main/resources/config/config-dev.yaml";
    public static ConfigData createConfig(String possiblePath) {
        String actualPath = possiblePath == null ? DEFAULT_PATH : possiblePath;

        try (InputStream in = Files.newInputStream(Path.of(actualPath))) {
            return new Yaml().loadAs(in, ConfigData.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}