package com.dokuny.accountmanagement.service;

import com.dokuny.accountmanagement.aop.AccountLockIdInterface;
import com.dokuny.accountmanagement.aop.UserLockIdInterface;
import com.dokuny.accountmanagement.exception.SpinLockException;
import com.dokuny.accountmanagement.service.LockService;
import com.dokuny.accountmanagement.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
@Aspect
public class LockAopAspect {

    private final LockService lockService;
    

    @Around("@annotation(com.dokuny.accountmanagement.aop.AccountLock) && args(request)")
    public Object spinLockByAccount(ProceedingJoinPoint pjp, AccountLockIdInterface request) throws Throwable {
        lockService.lock(request.getAccountNumber());

        try{
            return pjp.proceed();
        }finally {
            lockService.unlock(request.getAccountNumber());
        }
    }


}
