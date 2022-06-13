package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Account;
import lombok.*;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class DeleteAccount {
    @Getter
    @Setter
    public static class Request {

        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Length(max = 10, min = 10)
        private String accountNumber;


    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime unregisteredAt;

        public static DeleteAccount.Response of(Account account,Long userId) {

            return Response.builder()
                    .userId(userId)
                    .accountNumber(account.getAccountNumber())
                    .unregisteredAt(account.getUnregisteredAt())
                    .build();
        }
    }
}
