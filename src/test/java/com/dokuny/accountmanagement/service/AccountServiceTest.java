package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.config.policy.PolicyAccountProperties;
import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.dto.AccountDto;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.repository.AccountUserRepository;
import com.dokuny.accountmanagement.service.util.AccountNumGenerator;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.repository.AccountRepository;
import com.dokuny.accountmanagement.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    void createAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder().id(10L).build();

        Account account = Account.builder()
                .id(10L)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .accountUser(user)
                .accountNumber("1234567890")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(policyAccountProperties.getMax())
                .willReturn(10);

        given(accountRepository.countAccountByAccountUser_Id(anyLong()))
                .willReturn(9);

        given(accountNumGenerator.generateNumber())
                .willReturn("1111111111");

        given(accountRepository.existsAccountByAccountNumber(anyString()))
                .willReturn(false);

        given(accountRepository.save(any(Account.class)))
                .willReturn(account);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);



        //when
        AccountDto createAccount =
                accountService.createAccount(10L, 1000L);



        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertAll(
                () -> assertEquals("1111111111", captor.getValue().getAccountNumber()),
                () -> assertEquals(1000, createAccount.getBalance()),
                () -> assertEquals(10, createAccount.getAccountNumber().length()));
    }

    @Test
    @DisplayName("계좌 생성 실패 - 존재하지않는 사용자일 경우")
    void checkAccountUserByUSER_NOT_EXIST() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException ex = assertThrows(AccountException.class,
                () -> accountService.createAccount(10L, 1000L));

        //then
        assertEquals(ErrorCode.USER_NOT_EXIST, ex.getErrorCode());

    }

    @Test
    @DisplayName("계좌 성공 실패 - 사용자의 계좌 수가 최대일 경우")
    void checkAccountUserByUSER_MAX_ACCOUNT() {
        //give
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(AccountUser.builder().build()));

        given(accountRepository.countAccountByAccountUser_Id(anyLong()))
                .willReturn(10);

        //when
        AccountException ex = assertThrows(AccountException.class,
                () -> accountService.createAccount(10L, 1000L));
        //then
        assertEquals(ErrorCode.USER_MAX_ACCOUNT, ex.getErrorCode());
    }


    /**
     * UnregisterAccount Test
     */
    @Test
    @DisplayName("계좌 해지 성공")
    void unregisterAccountSuccess() {
        //given

        Optional<Account> account = Optional.of(Account.builder()
                .accountStatus(AccountStatus.IN_USE)
                .balance(0L)
                .accountUser(AccountUser.builder()
                        .id(10L)
                        .build())
                .build());

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(account);
        //when
        AccountDto dto =
                accountService.unregisterAccount(10L, "1234567890");

        //then
        assertAll(
                () -> assertEquals(AccountStatus.CLOSED, account.get().getAccountStatus()),
                () -> assertNotNull(dto.getUnregisteredAt())
        );
    }

    @Test
    @DisplayName("계좌 해지 실패 - 사용자가 없는 경우")
    void unregisterAccountFailByACCOUNT_NOT_EXIST() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException ex = assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_EXIST,ex.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 실패 - 계좌 소유주가 아닌 경우")
    void unregisterAccountFailByUSER_NOT_ACCOUNT_OWNER() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(0L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));

        //when
        AccountException ex = assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(20L, "1234567890"));
        //then
        assertEquals(ErrorCode.USER_NOT_ACCOUNT_OWNER,ex.getErrorCode());
    }

    @Test
    @DisplayName("계좌 해지 실패 - 이미 해지된 계좌인 경우")
    void unregisterAccountFailByACCOUNT_INVALID() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.CLOSED)
                        .balance(0L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        AccountException ex = assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
        //then
        assertEquals(ErrorCode.ACCOUNT_INVALID, ex.getErrorCode());

    }

    @Test
    @DisplayName("계좌 해지 실패 - 잔액이 있는 경우")
    void unregisterAccountFailByACCOUNT_REMAINED_BALANCE() {
        //given
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(100L)
                        .accountUser(AccountUser.builder()
                                .id(10L)
                                .build())
                        .build()));
        //when
        AccountException ex = assertThrows(AccountException.class, () ->
                accountService.unregisterAccount(10L, "1234567890"));
        //then
        assertEquals(ErrorCode.ACCOUNT_REMAINED_BALANCE,ex.getErrorCode());
    }


    /**
     * GetAccountAll Test
     */

    @Test
    @DisplayName("사용자의 모든 계좌 조회 성공")
    void getAccountAllSuccess() {

        //given
        List<Account> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(Account.builder().accountUser(AccountUser.builder().id(10L).build()).build());
        }

        given(accountUserRepository.existsById(anyLong()))
                .willReturn(true);

        given(accountRepository.findAllByAccountUser_Id(anyLong()))
                .willReturn(list);


        //when
        List<AccountDto> accounts = accountService.getAccountAll(10L);

        //then
        assertEquals(10, accounts.size());
    }

    @Test
    @DisplayName("사용자의 모든 계좌 조회 실패 - 존재하지 않는 사용자일 경우")
    void getAccountAllFailByUSER_NOT_EXIST() {
        //given
        given(accountUserRepository.existsById(anyLong()))
                .willReturn(false);

        //when
        AccountException ex = assertThrows(AccountException.class,
                () -> accountService.getAccountAll(10L));
        //then
        assertEquals(ErrorCode.USER_NOT_EXIST, ex.getErrorCode());
    }


}