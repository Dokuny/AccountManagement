package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {

    private String accountNumber;
    private TransactionType transactionType;
    private TransactionResultStatus transactionResultStatus;
    private String transactionId;
    private Long amount;
    private LocalDateTime transactedAt;

    public static TransactionDto of(Transaction transaction) {
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultStatus(transaction.getTransactionResultStatus())
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
