package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.config.JpaAuditingConfig;
import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.type.AccountStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


@Import(JpaAuditingConfig.class)
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUserRepository accountUserRepository;

    /**
     * createAccount Repository Test
     */

    @Test
    @DisplayName("중복된 계좌번호가 있는지 테스트")
    void existsAccountByAccountNumber() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .name("test")
                .build();

        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USE)
                .accountNumber("1234567891")
                .balance(1000L)
                .build();

        accountUserRepository.save(accountUser);
        accountRepository.save(account);

        //when
        boolean result1 =
                accountRepository.existsAccountByAccountNumber("1234567891");
        boolean result2 =
                accountRepository.existsAccountByAccountNumber("1234567890");

        //then
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("10개 이상의 계좌를 가지고 있는지")
    void countAccountByAccountUser_Id(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .name("test")
                .build();
        accountUserRepository.save(accountUser);

        for (int i = 0; i < 10; i++) {
            Account account = Account.builder()
                    .accountUser(accountUser)
                    .accountStatus(AccountStatus.IN_USE)
                    .accountNumber("123456789"+i)
                    .balance(1000L)
                    .build();
            accountRepository.save(account);
        }

        //when
        Integer count =
                accountRepository
                        .countAccountByAccountUser_Id(accountUser.getId());

        //then
        assertEquals(10,count);

    }

    /**
     *  getAccount Test
     */
    @Test
    @DisplayName("가지고 있는 계좌 전부 조회")
    void findAllByAccountUser_Id(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .name("test")
                .build();
        accountUserRepository.save(accountUser);

        for (int i = 0; i < 10; i++) {
            Account account = Account.builder()
                    .accountUser(accountUser)
                    .accountStatus(AccountStatus.IN_USE)
                    .accountNumber("123456789"+i)
                    .balance(1000L)
                    .build();
            accountRepository.save(account);
        }
        //when
        List<Account> accounts = accountRepository
                .findAllByAccountUser_Id(accountUser.getId()).get();

        //then
        assertEquals(10,accounts.size());

    }




}