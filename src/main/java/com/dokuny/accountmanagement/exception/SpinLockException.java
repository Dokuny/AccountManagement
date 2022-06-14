package com.dokuny.accountmanagement.exception;

import com.dokuny.accountmanagement.type.ErrorCode;

public class SpinLockException extends BaseException{
    public SpinLockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
