package com.dokuny.accountmanagement.service.aop;

import com.dokuny.accountmanagement.exception.SpinLockException;
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
public class RedisAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.dokuny.accountmanagement.service.aop.UserLock)")
    public Object spinLockByUser(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        RLock lock = redissonClient.getLock(args[0].toString());

        return spinLock(lock,pjp);
    }

    @Around("@annotation(com.dokuny.accountmanagement.service.aop.AccountLock)")
    public Object spinLockByAccount(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        RLock lock = redissonClient.getLock(args[1].toString());

        return spinLock(lock,pjp);
    }

    private Object spinLock(RLock lock,ProceedingJoinPoint pjp) throws Throwable {

        try {
            boolean isLock = lock.tryLock(60, 5, TimeUnit.SECONDS);

            if (!isLock) {
                throw new SpinLockException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            log.info("==========Lock Acquisition==========");
            return pjp.proceed();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            log.info("==========Lock Returned==========");
        }

        return null;
    }
}
