package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsAccountByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumberAndAccountUser_Id(String accountNumber, Long userId);
    Integer countAccountByAccountUser_Id(Long userId);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAllByAccountUser_Id(Long userId);
}
