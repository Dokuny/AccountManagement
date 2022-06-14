package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.service.aop.UserLock;

import com.dokuny.accountmanagement.config.policy.PolicyAccountProperties;
import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.repository.AccountUserRepository;
import com.dokuny.accountmanagement.repository.AccountRepository;
import com.dokuny.accountmanagement.service.util.AccountNumGenerator;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service

public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountNumGenerator accountNumGenerator;
    private final PolicyAccountProperties policyAccountProperties;

    @UserLock
    @Transactional
    public Account createAccount(
            Long userId, Long initialBalance) {

        // 유저 체크
        AccountUser accountUser = checkAccountUser(userId);

        // 계좌 생성
        Account account = Account.builder()
                .accountStatus(AccountStatus.IN_USE)
                .balance(initialBalance)
                .accountUser(accountUser)
                .accountNumber(generateAccountNum())
                .build();

        // 저장
        return accountRepository.save(account);
    }


    @Transactional(readOnly = true)
    public List<Account> getAccountAll(Long userId) {

        return  accountRepository.findAllByAccountUser_Id(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_EXIST));
    }

    @Transactional
    public Account unregisterAccount(Long userId, String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new AccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        AccountUser user = account.getAccountUser();

        if (!user.getId().equals(userId)) {
            throw new AccountException(ErrorCode.USER_NOT_ACCOUNT_OWNER);
        } else if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountException(ErrorCode.ACCOUNT_INVALID);
        } else if (!account.getBalance().equals(0L)) {
            throw new AccountException(ErrorCode.ACCOUNT_REMAINED_BALANCE);
        }

        account.close();

        return account;
    }


    private String generateAccountNum() {
        while (true) {
            String accountNumber = accountNumGenerator.generateNumber();

            if (!accountRepository
                    .existsAccountByAccountNumber(accountNumber)) {
                return accountNumber;
            }
        }
    }

    private AccountUser checkAccountUser(Long userId) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_EXIST));

        // 해당 유저가 계좌가 10개인지
        if (accountRepository.
                countAccountByAccountUser_Id(userId) >= policyAccountProperties.getMax()) {
            throw new AccountException(ErrorCode.USER_MAX_ACCOUNT);
        }

        return accountUser;
    }

}
