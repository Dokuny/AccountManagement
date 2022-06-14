package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class CancelTransaction {


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
   public static class Request {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Length(min = 10,max = 10,message = "계좌번호는 10자리여야 합니다.")
        private String accountNumber;

        @NotNull
        @Min(value = 100,message = "취소 금액은 100 부터 가능합니다.")
        private Long amount;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
   public static class Response{
        private String accountNumber;
        private TransactionResultStatus transactionResultStatus;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static Response of(Transaction transaction, String accountNumber) {
            return Response.builder()
                    .accountNumber(accountNumber)
                    .transactionId(transaction.getId())
                    .transactionResultStatus(transaction.getTransactionResultStatus())
                    .amount(transaction.getAmount())
                    .transactedAt(transaction.getTransactedAt())
                    .build();
        }

    }
}
