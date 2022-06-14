package com.dokuny.accountmanagement.service;


import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.exception.TransactionException;
import com.dokuny.accountmanagement.repository.AccountRepository;
import com.dokuny.accountmanagement.repository.TransactionRepository;
import com.dokuny.accountmanagement.service.aop.AccountLock;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.type.ErrorCode;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@RequiredArgsConstructor
@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @AccountLock
    @Transactional
    public Transaction useBalance(Long userId, String accountNumber, Long amount) {
        // 사용자 계좌 불러오기

        // 1. 계좌 조회
        Account account = accountRepository
                .findByAccountNumberAndAccountUser_Id(accountNumber, userId)
                .orElseThrow(() -> new TransactionException(ErrorCode.NO_SUCH_ACCOUNT));

        // 2. 예외 처리
        // 디비에서 조건에 해당되는 것만 가져올 수도 있지만 에러 코드로 어떤 상황인지 알리기 위해서 개별 처리
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new TransactionException(ErrorCode.ALREADY_CLOSED_ACCOUNT);
        } else if (account.getBalance() < amount) {
            throw new TransactionException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // 3. 계좌에서 잔액 삭감
        // dirty checking
        account.useBalance(amount);


        // 4. 트랜잭션 생성
        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.USE)
                .account(account)
                .amount(amount)
                .balanceSnapShot(account.getBalance())
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build();

        // 5. 트랜잭션 저장
        return transactionRepository.save(transaction);
    }

    @AccountLock
    @Transactional
    public Transaction cancelTransaction(
            String transactionId, String accountNumber, Long amount) {

        //1. 거래 조회
        Transaction transaction =
                transactionRepository
                        .findByIdAndAccount_AccountNumberAndTransactionType(
                                transactionId, accountNumber, TransactionType.USE)
                        .orElseThrow(() ->
                                new TransactionException(ErrorCode.TRANSACTION_NOT_EXIST));

        // 2. 예외처리
        // 사실 모두 DB 내에서 필터링 거쳐서 가져올 수 있습니다.
        if (!transaction.getAmount().equals(amount)) {
            throw new TransactionException(ErrorCode.CANCEL_AMOUNT_NOT_CORRECT);
        } else if (60 * 60 * 24 * 365 < ChronoUnit.SECONDS.between(
                transaction.getTransactedAt(), LocalDateTime.now())) {
            throw new TransactionException(ErrorCode.TRANSACTION_CANCEL_EXPIRED);
        }


        Account account = transaction.getAccount();

        account.addBalance(amount);

        return transactionRepository.save(Transaction.builder()
                .transactionType(TransactionType.CANCEL)
                .account(account)
                .amount(amount)
                .balanceSnapShot(account.getBalance())
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build());
    }

    @Transactional(readOnly = true)
    public Transaction checkTransaction(String transactionId) {

        return transactionRepository.findByIdForSimpleCheck(transactionId)
                .orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_EXIST));
    }
}
