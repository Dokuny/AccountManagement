package com.dokuny.accountmanagement.exception.advice;

import com.dokuny.accountmanagement.dto.ExceptionResponse;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.exception.BaseException;
import com.dokuny.accountmanagement.exception.SpinLockException;
import com.dokuny.accountmanagement.exception.TransactionException;
import com.dokuny.accountmanagement.type.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AccountException.class,TransactionException.class})
    protected ResponseEntity<ExceptionResponse> handleAccountException(BaseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.builder()
                        .errorCode(e.getErrorCode())
                        .timesStamp(e.getTimesStamp())
                        .errorMessage(e.getErrorMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ExceptionResponse> handleBeanValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ExceptionResponse.builder()
                        .errorCode(ErrorCode.BAD_REQUEST_PARAMETER)
                        .timesStamp(LocalDateTime.now())
                        .errorMessage(Objects.requireNonNull(e.getFieldError()).getDefaultMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

}
