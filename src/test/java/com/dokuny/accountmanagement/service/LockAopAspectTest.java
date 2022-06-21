package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.dto.UseBalanceTransaction;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    void lockAndUnlock() throws Throwable {
        //given
        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalanceTransaction.Request request
                = UseBalanceTransaction.Request.builder()
                .userId(1234L)
                .accountNumber("1234")
                .build();

        //when
        lockAopAspect.spinLockByAccount(proceedingJoinPoint, request);

        //then
        then(lockService).should(times(1)).lock(lockCaptor.capture());
        then(lockService).should(times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234", lockCaptor.getValue());
        assertEquals("1234", unlockCaptor.getValue());
    }

    @Test
    void lockAndUnlock_evenIfThrow() throws Throwable {
        //given
        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalanceTransaction.Request request
                = UseBalanceTransaction.Request.builder()
                .userId(1234L)
                .accountNumber("1234")
                .build();

        given(proceedingJoinPoint.proceed())
                .willThrow(new AccountException((ErrorCode.ACCOUNT_INVALID)));

        //when
        assertThrows(AccountException.class, () ->
                lockAopAspect.spinLockByAccount(proceedingJoinPoint, request));

        //then
        then(lockService).should(times(1)).lock(lockCaptor.capture());
        then(lockService).should(times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234", lockCaptor.getValue());
        assertEquals("1234", unlockCaptor.getValue());
    }
}