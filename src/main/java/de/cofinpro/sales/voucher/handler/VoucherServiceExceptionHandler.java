package de.cofinpro.sales.voucher.handler;

import de.cofinpro.sales.voucher.domain.VoucherServiceCustomErrorMessage;
import de.cofinpro.sales.voucher.exception.PasswordUpdateException;
import de.cofinpro.sales.voucher.exception.RoleUpdateException;
import de.cofinpro.sales.voucher.exception.UserAlreadyExistException;
import de.cofinpro.sales.voucher.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class VoucherServiceExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<VoucherServiceCustomErrorMessage> handleValidationError(MethodArgumentNotValidException e,
                                                                                  HttpServletRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        VoucherServiceCustomErrorMessage body = VoucherServiceCustomErrorMessage.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(body, status);
    }


    @ExceptionHandler({UserAlreadyExistException.class, RoleUpdateException.class, PasswordUpdateException.class})
    public ResponseEntity<VoucherServiceCustomErrorMessage> handleConflict(Exception e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        VoucherServiceCustomErrorMessage body = VoucherServiceCustomErrorMessage.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, status);
    }


    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<VoucherServiceCustomErrorMessage> handleNotFound(Exception e, HttpServletRequest request) {

        final HttpStatus status = HttpStatus.NOT_FOUND;
        VoucherServiceCustomErrorMessage body = VoucherServiceCustomErrorMessage.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(body, status);
    }
}
