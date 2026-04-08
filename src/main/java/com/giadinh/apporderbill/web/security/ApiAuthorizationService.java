package com.giadinh.apporderbill.web.security;

import com.giadinh.apporderbill.identity.IdentityComponent;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ApiAuthorizationService {
    private final IdentityComponent identityComponent;

    public ApiAuthorizationService(IdentityComponent identityComponent) {
        this.identityComponent = identityComponent;
    }

    public void requireView(String username, String functionName) {
        require(username, functionName, false);
    }

    public void requireOperate(String username, String functionName) {
        require(username, functionName, true);
    }

    private void require(String username, String functionName, boolean operate) {
        if (username == null || username.isBlank()) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }
        String normalizedUsername = username.trim();
        boolean userExists = identityComponent.getAllUsers().stream()
                .anyMatch(u -> normalizedUsername.equals(u.getUsername()));
        if (!userExists) {
            throw new DomainException(ErrorCode.AUTH_INVALID_CREDENTIALS);
        }

        boolean allowed = identityComponent.checkAccess(normalizedUsername, functionName, operate);
        if (!allowed) {
            throw new DomainException(ErrorCode.AUTH_FORBIDDEN);
        }
    }
}
