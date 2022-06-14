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


    @Test
    @DisplayName("Account 저장")
    void save() {
        //given
        Account account = Account.builder()
                .accountUser(null)
                .accountNumber("1234567890")
                .balance(1000L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        //when
        Account save = accountRepository.save(account);

        //then
        assertNotNull(save.getId());
    }


    @Test
    @DisplayName("계좌번호가 존재하는지 여부")
    void existsAccountByAccountNumber() {
        //given
        Account account = Account.builder()
                .accountUser(null)
                .accountStatus(AccountStatus.IN_USE)
                .accountNumber("1234567890")
                .balance(1000L)
                .build();

        accountRepository.save(account);

        //when
        boolean correct =
                accountRepository.existsAccountByAccountNumber("1234567890");
        boolean incorrect =
                accountRepository.existsAccountByAccountNumber("1234567891");

        //then
        assertTrue(correct);
        assertFalse(incorrect);
    }

    @Test
    @DisplayName("계좌번호와 사용자 아이디로 조회")
    void findByAccountNumberAndAccountUser_Id() {
//        //given

        AccountUser savedUser =
                accountUserRepository.save(AccountUser.builder().build());


        Account savedAccount = accountRepository.save(Account.builder()
                .accountUser(savedUser)
                .accountStatus(AccountStatus.IN_USE)
                .accountNumber("1234567890")
                .balance(1000L)
                .build());

        //when
        Account find =
                accountRepository
                        .findByAccountNumberAndAccountUser_Id(
                                "1234567890", savedUser.getId())
                        .orElseThrow();

        //then
        assertEquals(find, savedAccount);
    }

    @Test
    @DisplayName("사용자의 계좌 개수 조회")
    void countAccountByAccountUser_Id() {
        //given
        AccountUser user = accountUserRepository.save(AccountUser.builder()
                .name("test")
                .build());

        for (int i = 0; i < 10; i++) {
            Account account = Account.builder()
                    .accountUser(user)
                    .accountStatus(AccountStatus.IN_USE)
                    .accountNumber("123456789" + i)
                    .balance(1000L)
                    .build();
            accountRepository.save(account);
        }

        //when
        Integer count =
                accountRepository
                        .countAccountByAccountUser_Id(user.getId());

        //then
        assertEquals(10, count);
    }

    @Test
    @DisplayName("계좌번호로 계좌 조회")
    void findAllByAccountUser_Id() {
        //given
        AccountUser user =
                accountUserRepository.save(AccountUser.builder()
                        .name("test")
                        .build());

        Account account =
                accountRepository.save(Account.builder()
                        .accountUser(user)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build());

        //when
        Account findAccount =
                accountRepository
                        .findByAccountNumber(account.getAccountNumber()).orElseThrow();

        //then
        assertEquals(account, findAccount);
    }

    @Test
    @DisplayName("사용자 아이디로 계좌 모두 조회")
    void findByAccountNumber() {
        //given
        AccountUser user = accountUserRepository.save(AccountUser.builder()
                .name("test")
                .build());

        for (int i = 0; i < 10; i++) {
            Account account = Account.builder()
                    .accountUser(user)
                    .accountStatus(AccountStatus.IN_USE)
                    .accountNumber("123456789" + i)
                    .balance(1000L)
                    .build();
            accountRepository.save(account);
        }

        //when
        List<Account> accounts =
                accountRepository
                        .findAllByAccountUser_Id(user.getId())
                        .orElseThrow();

        //then
        assertEquals(10, accounts.size());
    }


}