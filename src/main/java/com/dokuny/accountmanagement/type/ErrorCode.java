package com.dokuny.accountmanagement.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_EXIST("사용자가 없습니다."),
    USER_MAX_ACCOUNT("사용자의 계좌 수가 최대치입니다."),
    USER_NOT_ACCOUNT_OWNER("계좌 소유주가 아닙니다."),
    ACCOUNT_NOT_EXIST("계좌번호에 해당하는 계좌가 없습니다."),
    ACCOUNT_INVALID("유효하지 않은 계좌입니다."),
    ACCOUNT_REMAINED_BALANCE("계좌에 잔액이 남아있습니다."),
    LOCK_ACQUISITION_FAILED("락을 얻는데 실패했습니다.");


    private String description;
}
