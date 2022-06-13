package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Account;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {

    @Getter @Setter
    public static class Request{

        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Min(100)
        private Long initialBalance;


    }

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response of(Long userId,Account account) {

            return Response.builder()
                    .userId(userId)
                    .accountNumber(account.getAccountNumber())
                    .registeredAt(account.getRegisteredAt())
                    .build();
        }
    }

}
