package com.dokuny.accountmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTestService {

    private final RedissonClient redissonClient;

    public String getLock() {
        // Lock 가져오기
        RLock lock = redissonClient.getLock("sampleLock");

        try {
            // lock이 사용중이면 1초 동안 Lock을 기다리고 lock을 얻으면 5초 동안 Lock을 가지고 있다가 해제
            // 명시적으로 unlock을 안하면 5초동안 가지고 있습니다.
            boolean isLock = lock.tryLock(5, 10, TimeUnit.SECONDS);


            if (!isLock) {
                log.error("================== Lock acquisition failed =================");
                return "Lock failed";
            }

        } catch (Exception e) {
            log.error("Redis lock failed");
        }

        return "Lock Success";
    }

}
