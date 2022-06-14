package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.config.JpaAuditingConfig;
import com.dokuny.accountmanagement.domain.AccountUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@Import(JpaAuditingConfig.class)
@DataJpaTest
class AccountUserRepositoryTest {

    @Autowired
    private AccountUserRepository accountUserRepository;


    @Test
    @DisplayName("User 저장")
    void save(){
        //given
        AccountUser user = AccountUser.builder()
                .name("test")
                .build();

        //when
        AccountUser savedUser = accountUserRepository.save(user);

        //then
        assertNotNull(savedUser.getId());
    }
}