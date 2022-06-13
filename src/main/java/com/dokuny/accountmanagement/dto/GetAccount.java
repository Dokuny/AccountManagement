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

        public static GetAccount.Response of(Account account) {

            return Response.builder()
                    .accountNumber(account.getAccountNumber())
                    .balance(account.getBalance())
                    .build();
        }
        public static List<Response> of(List<Account> accounts) {
            ArrayList<Response> list = new ArrayList<>();

            for (Account account : accounts) {
                list.add(Response.of(account));
            }
            return list;
        }


    }
}
