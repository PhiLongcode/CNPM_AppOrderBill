package com.giadinh.apporderbill.web;

import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.DomainMessages;
import com.giadinh.apporderbill.shared.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;
import java.util.Locale;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final URI PROBLEM_VALIDATION = URI.create("about:blank#validation");
    private static final URI PROBLEM_DOMAIN = URI.create("about:blank#domain");
    private static final URI PROBLEM_INTERNAL = URI.create("about:blank#internal");

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public ApiExceptionHandler(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomain(DomainException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        ErrorCode code = ex.getErrorCode();
        String detail = messageSource.getMessage(
                DomainMessages.messageKey(code),
                ex.getMessageArgs(),
                ex.getMessage(),
                locale);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(code.httpStatus(), detail);
        pd.setTitle(code.httpStatus().getReasonPhrase());
        pd.setType(PROBLEM_DOMAIN);
        pd.setProperty("code", code.code());
        ex.getProperties().forEach(pd::setProperty);

        return ResponseEntity.status(code.httpStatus()).body(pd);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String detail = messageSource.getMessage(
                DomainMessages.messageKey(ErrorCode.RESOURCE_NOT_FOUND),
                null,
                ex.getMessage(),
                locale);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        pd.setTitle(HttpStatus.NOT_FOUND.getReasonPhrase());
        pd.setType(URI.create("about:blank#not-found"));
        pd.setProperty("code", ErrorCode.RESOURCE_NOT_FOUND.code());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        FieldError fe = ex.getBindingResult().getFieldError();
        String field = fe != null ? fe.getField() : null;
        String defaultMsg = fe != null && fe.getDefaultMessage() != null ? fe.getDefaultMessage() : ex.getMessage();

        String detail = messageSource.getMessage(
                DomainMessages.messageKey(ErrorCode.COMMON_VALIDATION_FAILED),
                null,
                defaultMsg,
                locale);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        pd.setType(PROBLEM_VALIDATION);
        pd.setProperty("code", ErrorCode.COMMON_VALIDATION_FAILED.code());
        if (field != null) {
            pd.setProperty("field", field);
        }
        return ResponseEntity.badRequest().body(pd);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        Locale locale = localeResolver.resolveLocale(request);
        String detail = messageSource.getMessage(
                DomainMessages.messageKey(ErrorCode.INTERNAL_ERROR),
                null,
                ErrorCode.INTERNAL_ERROR.name(),
                locale);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail);
        pd.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        pd.setType(PROBLEM_INTERNAL);
        pd.setProperty("code", ErrorCode.INTERNAL_ERROR.code());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }
}
