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
    LOCK_ACQUISITION_FAILED("락을 얻는데 실패했습니다."),
    NO_SUCH_ACCOUNT("조건에 해당하는 계좌를 찾을 수 없습니다."),
    ALREADY_CLOSED_ACCOUNT("해지된 계좌 입니다."),
    NOT_ENOUGH_BALANCE("계좌 잔액이 부족합니다."),
    TRANSACTION_NOT_EXIST("거래가 존재하지 않습니다."),
    CANCEL_AMOUNT_NOT_CORRECT("취소하려는 거래와 취소 금액이 일치하지 않습니다."),
    TRANSACTION_CANCEL_EXPIRED("거래 취소 기간이 지났습니다."),
    BAD_REQUEST_PARAMETER("요청 파라미터가 잘못되었습니다.");


    private String description;
}
