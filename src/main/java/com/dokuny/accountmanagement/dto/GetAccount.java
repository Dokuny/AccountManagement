package com.dokuny.accountmanagement.dto;

import com.dokuny.accountmanagement.domain.Account;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class GetAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private Long balance;

        public static GetAccount.Response of(AccountDto dto) {

            return Response.builder()
                    .accountNumber(dto.getAccountNumber())
                    .balance(dto.getBalance())
                    .build();
        }

        public static List<Response> of(List<AccountDto> dtos) {
            ArrayList<Response> list = new ArrayList<>();

            for (AccountDto dto : dtos) {
                list.add(Response.of(dto));
            }
            return list;
        }
    }
}
