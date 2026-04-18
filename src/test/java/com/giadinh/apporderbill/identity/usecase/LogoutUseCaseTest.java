package com.giadinh.apporderbill.identity.usecase;

import com.giadinh.apporderbill.identity.usecase.dto.LogoutInput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LogoutUseCaseTest {

    @Test
    void execute_trimsUsername() {
        LogoutUseCase useCase = new LogoutUseCase();
        var out = useCase.execute(new LogoutInput("  admin  "));
        assertEquals("admin", out.getUsername());
    }

    @Test
    void execute_blankUsername_throws() {
        LogoutUseCase useCase = new LogoutUseCase();
        DomainException ex = assertThrows(DomainException.class,
                () -> useCase.execute(new LogoutInput("   ")));
        assertEquals(ErrorCode.LOGOUT_USERNAME_REQUIRED, ex.getErrorCode());
    }

    @Test
    void execute_nullInput_throws() {
        LogoutUseCase useCase = new LogoutUseCase();
        assertThrows(DomainException.class, () -> useCase.execute(null));
    }
}
