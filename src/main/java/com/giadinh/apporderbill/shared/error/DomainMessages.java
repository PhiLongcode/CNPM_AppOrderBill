package com.giadinh.apporderbill.shared.error;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * Resolves user-facing error text for JavaFX and non-Spring callers. Uses UTF-8 {@code messages.properties}.
 */
public final class DomainMessages {

    private static final Properties PROPS = new Properties();

    static {
        try (var in = DomainMessages.class.getClassLoader().getResourceAsStream("messages.properties")) {
            if (in != null) {
                PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DomainMessages() {
    }

    public static String messageKey(ErrorCode code) {
        return "error." + code.name();
    }

    public static String format(ErrorCode code, Object... args) {
        String key = messageKey(code);
        String pattern = PROPS.getProperty(key, key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }

    public static String format(DomainException ex) {
        return format(ex.getErrorCode(), ex.getMessageArgs());
    }

    /** Arbitrary bundle key (e.g. {@code success.*}). */
    public static String formatKey(String key, Object... args) {
        String pattern = PROPS.getProperty(key, key);
        if (args == null || args.length == 0) {
            return pattern;
        }
        return MessageFormat.format(pattern, args);
    }
}
