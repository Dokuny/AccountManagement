package com.dokuny.accountmanagement.exception;

import com.dokuny.accountmanagement.type.ErrorCode;

public class TransactionException extends BaseException{
    public TransactionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
