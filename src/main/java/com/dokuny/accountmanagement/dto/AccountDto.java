package com.dokuny.accountmanagement.dto;


import com.dokuny.accountmanagement.domain.Account;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

    private Long userId;
    private String accountNumber;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    public static AccountDto of(Account account) {
        return AccountDto.builder()
                .userId(account.getAccountUser().getId())
                .balance(account.getBalance())
                .accountNumber(account.getAccountNumber())
                .registeredAt(account.getRegisteredAt())
                .unregisteredAt(account.getUnregisteredAt())
                .build();
    }

    public static List<AccountDto> of(List<Account> accounts) {
        return accounts.stream().map(AccountDto::of).collect(Collectors.toList());
    }
}
