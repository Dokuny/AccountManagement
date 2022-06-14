package com.dokuny.accountmanagement.exception;

import com.dokuny.accountmanagement.type.ErrorCode;

public class AccountException extends BaseException{
    public AccountException(ErrorCode errorCode) {
        super(errorCode);
    }
}
