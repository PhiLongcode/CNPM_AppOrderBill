package com.giadinh.apporderbill.shared.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataModeConfig {
    public enum Mode {
        REAL,
        DEMO
    }

    private static final Path CONFIG_FILE = Path.of("output", "data-mode.cfg");
    private final Mode mode;

    public DataModeConfig(Mode mode) {
        this.mode = mode == null ? Mode.REAL : mode;
    }

    public static DataModeConfig load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                String raw = Files.readString(CONFIG_FILE).trim().toUpperCase();
                return new DataModeConfig("DEMO".equals(raw) ? Mode.DEMO : Mode.REAL);
            }
        } catch (IOException ignored) {
        }
        return new DataModeConfig(Mode.REAL);
    }

    public static void save(Mode mode) {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            Files.writeString(CONFIG_FILE, mode == Mode.DEMO ? "DEMO" : "REAL");
        } catch (IOException ignored) {
        }
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isDemo() {
        return mode == Mode.DEMO;
    }
}

