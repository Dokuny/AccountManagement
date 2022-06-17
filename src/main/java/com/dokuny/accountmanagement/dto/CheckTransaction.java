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


        public static Response of(TransactionDto dto) {
            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .transactionType(dto.getTransactionType())
                    .transactionResultStatus(dto.getTransactionResultStatus())
                    .transactionId(dto.getTransactionId())
                    .amount(dto.getAmount())
                    .transactedAt(dto.getTransactedAt())
                    .build();
        }
    }
}
