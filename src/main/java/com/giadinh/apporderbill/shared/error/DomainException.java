package com.giadinh.apporderbill.shared.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Business / validation failure with a stable {@link ErrorCode}. User-facing text comes from
 * message bundles ({@code error.&lt;ENUM_NAME&gt;}), not from exception message strings.
 */
public final class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] messageArgs;
    private final Map<String, Object> properties;

    public DomainException(ErrorCode errorCode) {
        this(errorCode, null, null, null);
    }

    public DomainException(ErrorCode errorCode, Object... messageArgs) {
        this(errorCode, messageArgs, null, null);
    }

    public DomainException(ErrorCode errorCode, Object[] messageArgs, String technicalDetail, Map<String, Object> properties) {
        super(technicalDetail);
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode");
        this.messageArgs = messageArgs == null ? new Object[0] : messageArgs.clone();
        this.properties = properties == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getMessageArgs() {
        return messageArgs.clone();
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String getMessage() {
        return DomainMessages.format(errorCode, messageArgs);
    }
}
