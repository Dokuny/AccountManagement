package com.dokuny.accountmanagement.repository;

import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {


    Optional<Transaction> findByIdAndAccount_AccountNumberAndTransactionType(
            String transactionId, String accountNumber, TransactionType transactionType);

    @Query("select t from Transaction t join fetch t.account where t.id = :id")
    Optional<Transaction> findByIdForSimpleCheck(@Param("id") String transactionId);
}
