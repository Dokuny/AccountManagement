package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.config.JpaAuditingConfig;
import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import static org.junit.jupiter.api.Assertions.*;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUserRepository accountUserRepository;


    @Test
    @DisplayName("거래 저장")
    void save() {
        //given
        Transaction transaction = Transaction.builder()
                .account(null)
                .build();

        //when
        Transaction save = transactionRepository.save(transaction);

        //then
        assertNotNull(save.getId());
    }

    @Test
    @DisplayName("거래 아이디,계좌번호,거래 타입으로 조회")
    void findByIdAndAccount_AccountNumberAndTransactionType() {
        //given
        AccountUser user = AccountUser.builder().build();

        accountUserRepository.save(user);

        Account account = Account.builder()
                .accountStatus(AccountStatus.IN_USE)
                .accountUser(user)
                .accountNumber("1234567891")
                .balance(1000L)
                .build();

        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.USE)
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .account(account)
                .amount(500L)
                .balanceSnapShot(500L)
                .build();

        transactionRepository.save(transaction);

        //when
        Transaction result = transactionRepository
                .findByIdAndAccount_AccountNumberAndTransactionType(
                        transaction.getId(),
                        account.getAccountNumber(),
                        transaction.getTransactionType()).orElseThrow();

        //then
        assertEquals(result,transaction);
    }


    @Test
    @DisplayName("거래 아이디로 조회")
    void findByIdForSimpleCheck() {
        //given
        AccountUser user = AccountUser.builder().build();

        accountUserRepository.save(user);

        Account account = Account.builder()
                .accountStatus(AccountStatus.IN_USE)
                .accountUser(user)
                .accountNumber("1234567891")
                .balance(1000L)
                .build();

        accountRepository.save(account);

        Transaction origin = Transaction.builder()
                .transactionType(TransactionType.USE)
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .account(account)
                .amount(500L)
                .balanceSnapShot(500L)
                .build();

        transactionRepository.save(origin);

        //when
        Transaction findWithFetch = transactionRepository
                .findByIdForSimpleCheck(origin.getId()).orElseThrow();

        Account fetchAccount = findWithFetch.getAccount();

        //then
        assertEquals(fetchAccount, account);
        assertEquals(findWithFetch, origin);

    }


}