package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

public class CheckTransaction {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
   public static class Response{
        private String accountNumber;
        private TransactionType transactionType;
        private TransactionResultStatus transactionResultStatus;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static Response of(Transaction transaction) {
            return Response.builder()
                    .accountNumber(transaction.getAccount().getAccountNumber())
                    .transactionType(transaction.getTransactionType())
                    .transactionResultStatus(transaction.getTransactionResultStatus())
                    .transactionId(transaction.getId())
                    .amount(transaction.getAmount())
                    .transactedAt(transaction.getTransactedAt())
                    .build();
        }
    }
}
