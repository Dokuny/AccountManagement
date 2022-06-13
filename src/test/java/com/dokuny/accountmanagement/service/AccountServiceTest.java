package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.config.policy.PolicyAccountProperties;
import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.exception.SpinLockException;
import com.dokuny.accountmanagement.repository.AccountUserRepository;
import com.dokuny.accountmanagement.service.util.AccountNumGenerator;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountNumGenerator accountNumGenerator;

    @Mock
    private PolicyAccountProperties policyAccountProperties;

    @InjectMocks
    private AccountService accountService;


    /**
     * CreateAccount Test
     */
    @Test
    @DisplayName("계좌 생성 성공")
    void createAccountSucess() throws InterruptedException {
        //given
        AccountUser user = AccountUser
                .builder()
                .id(10L)
                .name("test")
                .build();

        given(accountUserRepository.findById(10L))
                .willReturn(Optional.of(user));

        given(policyAccountProperties.getMax())
                .willReturn(10);

        given(accountRepository.save(any(Account.class)))
                .willReturn(Account.builder()
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L)
                        .accountUser(user)
                        .accountNumber("1234567890")
                        .build());

        //when
        Account account =
                accountService.createAccount(10L, 1000L);

        //then
        assertAll(
                () -> assertEquals(1000, account.getBalance()),
                () -> assertEquals(10, account.getAccountNumber().length()),
                () -> assertEquals(AccountStatus.IN_USE, account.getAccountStatus()),
                () -> assertEquals(user, account.getAccountUser())
        );
    }

    @Test
    @DisplayName("계좌 성공 실패 - 유저 없음")
    void creatAccountFailByNotExistUser() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.ofNullable(null));
        //when
        //then
        assertThrows(AccountException.class,
                () -> accountService.createAccount(10L, 1000L));
    }

    @Test
    @DisplayName("계좌 성공 실패 - 계좌 수 최대")
    void creatAccountFailByMaxAccount() {
        //given
        AccountUser user = AccountUser
                .builder()
                .id(10L)
                .name("test")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countAccountByAccountUser_Id(anyLong()))
                .willReturn(10);

        //when
        //then
        assertThrows(AccountException.class,
                () -> accountService.createAccount(10L, 1000L));
    }


    /**
     * unregisterAccount Test
     */
    @Test
    @DisplayName("계좌 해지 성공")
    void unregisterAccountSuccess() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(0L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        Account account =
                accountService.unregisterAccount(10L, "1234567890");

        //then
        assertAll(
                () -> assertEquals(AccountStatus.CLOSED, account.getAccountStatus()),
                () -> assertNotNull(account.getUnregisteredAt())
        );
    }

    @Test
    @DisplayName("계좌 해지 실패 - 사용자가 없는 경우")
    void unregisterAccountFailByNoUser() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.ofNullable(null));
        //when
        //then
        assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌 소유주가 아닌 경우")
    void unregisterAccountFailByOwner() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(0L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(20L, "1234567890"));
    }

    @Test
    @DisplayName("계좌 해지 실패 - 이미 해지된 계좌인 경우")
    void unregisterAccountFailByInvalidAccount() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.CLOSED)
                        .balance(0L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
    }

    @Test
    @DisplayName("계좌 해지 실패 - 잔액이 있는 경우")
    void unregisterAccountFailByBalanceExist() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(500L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
    }


    /**
     * getAccount Test
     */

    @Test
    @DisplayName("가지고 있는 계좌 전부 조회")
    void getAccountAllSuccess() {
        //given

        List<Account> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(Account.builder().build());
        }

        given(accountRepository.findAllByAccountUser_Id(anyLong()))
                .willReturn(Optional.of(list));

        //when
        List<Account> accounts = accountService.getAccountAll(10L);

        //then
        assertEquals(10, accounts.size());
    }





}