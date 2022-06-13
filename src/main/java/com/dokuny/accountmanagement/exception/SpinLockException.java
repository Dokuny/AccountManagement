package com.dokuny.accountmanagement.exception;


import com.dokuny.accountmanagement.type.ErrorCode;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Builder
public class SpinLockException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;
    private LocalDateTime timesStamp;

    public SpinLockException(ErrorCode errorCode) {
        this.errorCode=errorCode;
        this.errorMessage = errorCode.getDescription();
        this.timesStamp = LocalDateTime.now();
        log.error("SpinLockException {}",errorCode);
    }
}
