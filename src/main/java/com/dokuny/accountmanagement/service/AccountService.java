package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.dto.AccountDto;

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


    @Transactional
    public AccountDto createAccount(
            Long userId, Long initialBalance) {

        AccountUser accountUser = checkAccountUser(userId);

        return AccountDto.of(
                accountRepository.save(
                        Account.builder()
                                .accountStatus(AccountStatus.IN_USE)
                                .balance(initialBalance)
                                .accountUser(accountUser)
                                .accountNumber(generateAccountNum())
                                .build())
        );
    }


    @Transactional(readOnly = true)
    public List<AccountDto> getAccountAll(Long userId) {
        if (!accountUserRepository.existsById(userId)) {
            throw new AccountException(ErrorCode.USER_NOT_EXIST);
        }

        return AccountDto.of(accountRepository.findAllByAccountUser_Id(userId));
    }



    @Transactional
    public AccountDto unregisterAccount(Long userId, String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_EXIST));

        if (!account.getAccountUser().getId().equals(userId)) {
            throw new AccountException(ErrorCode.USER_NOT_ACCOUNT_OWNER);
        } else if (account.getAccountStatus() == AccountStatus.CLOSED) {
            throw new AccountException(ErrorCode.ACCOUNT_INVALID);
        } else if (!account.getBalance().equals(0L)) {
            throw new AccountException(ErrorCode.ACCOUNT_REMAINED_BALANCE);
        }

        account.close();

        return AccountDto.of(account);
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

        // ?????? ????????? ????????? 10?????????
        if (accountRepository.
                countAccountByAccountUser_Id(userId) >= policyAccountProperties.getMax()) {
            throw new AccountException(ErrorCode.USER_MAX_ACCOUNT);
        }

        return accountUser;
    }

}
