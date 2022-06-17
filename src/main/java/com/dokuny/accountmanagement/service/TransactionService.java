package com.dokuny.accountmanagement.service;


import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.dto.TransactionDto;
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
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        Account account = accountRepository
                .findByAccountNumberAndAccountUser_Id(accountNumber, userId)
                .orElseThrow(() -> new TransactionException(ErrorCode.NO_SUCH_ACCOUNT));


        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new TransactionException(ErrorCode.ALREADY_CLOSED_ACCOUNT);
        } else if (account.getBalance() < amount) {
            throw new TransactionException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        account.useBalance(amount);

        return TransactionDto.of(transactionRepository.save(Transaction.builder()
                .transactionType(TransactionType.USE)
                .account(account)
                .amount(amount)
                .balanceSnapShot(account.getBalance())
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build()));
    }

    @AccountLock
    @Transactional
    public TransactionDto cancelTransaction(
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

        return TransactionDto.of(transactionRepository.save(Transaction.builder()
                .transactionType(TransactionType.CANCEL)
                .account(account)
                .amount(amount)
                .balanceSnapShot(account.getBalance())
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build()));
    }

    @Transactional(readOnly = true)
    public TransactionDto checkTransaction(String transactionId) {

        return TransactionDto.of(transactionRepository.findByIdForSimpleCheck(transactionId)
                .orElseThrow(() -> new TransactionException(ErrorCode.TRANSACTION_NOT_EXIST)));
    }
}
