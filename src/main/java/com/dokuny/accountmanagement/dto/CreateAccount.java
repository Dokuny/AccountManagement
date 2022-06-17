package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Account;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request{

        @NotNull
        @Min(value = 1,message = "부적절한 ID값 입니다.")
        private Long userId;

        @NotNull
        @Min(value = 100,message = "초기 잔고는 100 이상부터 가능합니다.")
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

        public static Response of(AccountDto accountDto) {

            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt())
                    .build();
        }
    }

}
