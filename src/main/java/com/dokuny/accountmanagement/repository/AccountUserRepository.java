package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser,Long> {

    Optional<AccountUser> findByName(String name);
}
