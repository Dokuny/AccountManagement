package com.dokuny.accountmanagement.exception.advice;

import com.dokuny.accountmanagement.dto.ExceptionResponse;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.exception.SpinLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    protected ResponseEntity<ExceptionResponse> handleAccountException(AccountException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.builder()
                        .errorCode(e.getErrorCode())
                        .timesStamp(e.getTimesStamp())
                        .errorMessage(e.getErrorMessage())
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    @ExceptionHandler(SpinLockException.class)
    protected ResponseEntity<ExceptionResponse> handleSpinLockException(SpinLockException e) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.builder()
                        .errorCode(e.getErrorCode())
                        .timesStamp(e.getTimesStamp())
                        .errorMessage(e.getErrorMessage())
                        .status(HttpStatus.LOCKED.value())
                        .build());
    }
}
