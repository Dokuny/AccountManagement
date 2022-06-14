package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.exception.TransactionException;
import com.dokuny.accountmanagement.repository.AccountRepository;
import com.dokuny.accountmanagement.repository.TransactionRepository;
import com.dokuny.accountmanagement.type.AccountStatus;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;


    /**
     * UseBalance Test
     */

    @Test
    @DisplayName("잔액 사용 성공")
    void useBalanceSuccess() {
        //given
        Account account = Account.builder()
                .balance(1000L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        Transaction transaction = Transaction.builder()
                .transactionType(TransactionType.USE)
                .account(account)
                .amount(500L)
                .balanceSnapShot(500L)
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build();

        given(accountRepository.
                findByAccountNumberAndAccountUser_Id(anyString(), anyLong()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any(Transaction.class)))
                .willReturn(transaction);


        //when
        Transaction save =
                transactionService.useBalance(anyLong(), anyString(), 500L);

        //then
        assertEquals(500L, save.getAccount().getBalance());
    }

    @Test
    @DisplayName("잔액 사용 실패 - 사용자가 없거나 계좌와 사용자가 일치하지 않는 경우 ")
    void useBalanceFailByNO_SUCH_ACCOUNT() {
        //given
        given(accountRepository.
                findByAccountNumberAndAccountUser_Id(anyString(), anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThrows(TransactionException.class, () ->
                transactionService.
                        useBalance(10L, "test", 500L));

    }

    @Test
    @DisplayName("잔액 사용 실패 - 계좌가 해지된 경우")
    void useBalanceFailByALREADY_CLOSED_ACCOUNT() {
        //given
        Account account = Account.builder()
                .balance(1000L)
                .accountStatus(AccountStatus.CLOSED)
                .build();

        given(accountRepository.
                findByAccountNumberAndAccountUser_Id(anyString(), anyLong()))
                .willReturn(Optional.of(account));

        //when
        //then
        assertThrows(TransactionException.class, () ->
                transactionService.
                        useBalance(10L, "test", 500L));
    }

    @Test
    @DisplayName("잔액 사용 실패 - 잔액이 적은 경우")
    void useBalanceFailByNOT_ENOUGH_BALANCE() {
        //given
        Account account = Account.builder()
                .balance(100L)
                .accountStatus(AccountStatus.IN_USE)
                .build();

        given(accountRepository.
                findByAccountNumberAndAccountUser_Id(anyString(), anyLong()))
                .willReturn(Optional.of(account));

        //when
        //then
        assertThrows(TransactionException.class, () ->
                transactionService.
                        useBalance(10L, "test", 500L));
    }

    /**
     * cancelTransaction Test
     */

    @Test
    @DisplayName("거래 취소 성공")
    void cancelTransactionSuccess() {
        //given
        LocalDateTime time = LocalDateTime.now();

        Account account = Account.builder()
                .balance(500L)
                .build();

        Transaction transaction = Transaction.builder()
                .amount(500L)
                .transactionType(TransactionType.USE)
                .balanceSnapShot(500L)
                .transactedAt(time)
                .account(account)
                .build();

        Transaction cancelTransaction = Transaction.builder()
                .transactionType(TransactionType.CANCEL)
                .account(account)
                .amount(500L)
                .balanceSnapShot(1000L)
                .transactionResultStatus(TransactionResultStatus.SUCCESS)
                .build();

        given(transactionRepository
                .findByIdAndAccount_AccountNumberAndTransactionType(
                        anyString(), anyString(), any()))
                .willReturn(Optional.of(transaction));

        given(transactionRepository.save(any(Transaction.class)))
                .willReturn(cancelTransaction);

        //when
        Transaction result =
                transactionService
                        .cancelTransaction(
                                "test", "test", 500L);

        //then
        assertEquals(1000L, result.getAccount().getBalance());
    }

    @Test
    @DisplayName("거래 취소 실패 - 거래한지 1년 경과")
    void cancelTransactionFailByTRANSACTION_CANCEL_EXPIRED() {
        //given
        LocalDateTime time = LocalDateTime.now().minusYears(2);

        given(transactionRepository
                .findByIdAndAccount_AccountNumberAndTransactionType(
                        anyString(), anyString(), any()))
                .willReturn(Optional.of(Transaction.builder()
                        .amount(500L)
                        .balanceSnapShot(500L)
                        .transactedAt(time)
                        .account(Account.builder()
                                .balance(500L)
                                .build())
                        .build()));

        //when
        //then
        assertThrows(TransactionException.class, () -> transactionService
                .cancelTransaction("test", "test", 500L));
    }

    @Test
    @DisplayName("거래 취소 실패 - 취소 금액 불일치")
    void cancelTransactionFailByCANCEL_AMOUNT_NOT_CORRECT() {
        LocalDateTime time = LocalDateTime.now();
        //given
        given(transactionRepository
                .findByIdAndAccount_AccountNumberAndTransactionType(
                        anyString(), anyString(), any()))
                .willReturn(Optional.of(Transaction.builder()
                        .amount(400L)
                        .balanceSnapShot(500L)
                        .transactedAt(time)
                        .account(Account.builder()
                                .balance(500L)
                                .build())
                        .build()));

        //when
        //then
        assertThrows(TransactionException.class, () -> transactionService
                .cancelTransaction("test", "test", 500L));
    }


    /**
     * checkTransaction Test
     */

    @Test
    @DisplayName("거래 아이디로 거래 조회")
    void checkTransaction() {
        //given
        given(transactionRepository.findByIdForSimpleCheck(anyString()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThrows(TransactionException.class,
                () -> transactionService.checkTransaction("01234567890"));
    }
}