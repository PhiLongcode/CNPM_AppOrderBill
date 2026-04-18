package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.usecase.dto.LogoutInput;
import com.giadinh.apporderbill.identity.usecase.dto.LogoutOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

/**
 * Domain logout: validates username present. No server session in POS; callers show login again.
 */
public class LogoutUseCase {

    public LogoutOutput execute(LogoutInput input) {
        if (input == null || input.getUsername() == null || input.getUsername().isBlank()) {
            throw new DomainException(ErrorCode.LOGOUT_USERNAME_REQUIRED);
        }
        return new LogoutOutput(input.getUsername().trim());
    }
}
