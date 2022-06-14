package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class UseBalanceTransaction {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @NotNull
        @Min(value = 1,message = "부적절한 ID 값 입니다.")
        private Long userId;

        @NotBlank
        @Length(max = 10,min = 10,message = "계좌 번호는 10자리여야 합니다.")
        private String accountNumber;

        @NotNull
        @Min(value = 100,message = "사용 금액은 100 부터 가능합니다.")
        private Long amount;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private TransactionResultStatus transactionResultStatus;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static Response of(Transaction transaction,String accountNumber,Long amount) {
            return Response.builder()
                    .accountNumber(accountNumber)
                    .transactionResultStatus(transaction.getTransactionResultStatus())
                    .transactionId(transaction.getId())
                    .amount(amount)
                    .transactedAt(transaction.getTransactedAt())
                    .build();
        }

    }
}
