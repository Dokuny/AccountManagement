package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser,Long> {}
