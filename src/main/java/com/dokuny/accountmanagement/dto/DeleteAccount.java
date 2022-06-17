package com.dokuny.accountmanagement.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


public class DeleteAccount {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        @NotNull
        @Min(value = 1,message = "부적절한 ID 값 입니다.")
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

        public static DeleteAccount.Response of(AccountDto dto) {

            return Response.builder()
                    .userId(dto.getUserId())
                    .accountNumber(dto.getAccountNumber())
                    .unregisteredAt(dto.getUnregisteredAt())
                    .build();
        }
    }
}
