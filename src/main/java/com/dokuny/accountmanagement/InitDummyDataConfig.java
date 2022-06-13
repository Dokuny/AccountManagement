package com.dokuny.accountmanagement;

import com.dokuny.accountmanagement.domain.AccountUser;
import com.dokuny.accountmanagement.repository.AccountUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Configuration
public class InitDummyDataConfig {

    private final AccountUserRepository accountUserRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 10; i++) {
            AccountUser user = AccountUser.builder()
                    .name("test"+i)
                    .build();

            accountUserRepository.save(user);
        }
    }
}
