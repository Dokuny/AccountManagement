package com.dokuny.accountmanagement.exception;

import com.dokuny.accountmanagement.type.ErrorCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Slf4j
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;
    private final LocalDateTime timesStamp;

    public BaseException(ErrorCode errorCode) {
        this.errorCode=errorCode;
        this.errorMessage = errorCode.getDescription();
        this.timesStamp = LocalDateTime.now();
        log.error("{} {}",this.getClass().getName(),errorCode);
    }

}
