package com.dokuny.accountmanagement.controller;

import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.dto.CreateAccount;
import com.dokuny.accountmanagement.dto.DeleteAccount;
import com.dokuny.accountmanagement.dto.GetAccount;
import com.dokuny.accountmanagement.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {

        Account account = accountService.createAccount(
                request.getUserId(), request.getInitialBalance());

        return CreateAccount.Response.of(request.getUserId(), account);
    }

    @GetMapping("/{userId}")
    public List<GetAccount.Response> getAccountAll(@PathVariable Long userId) {

        List<Account> accounts = accountService.getAccountAll(userId);

        return GetAccount.Response.of(accounts);
    }

    @DeleteMapping
    public DeleteAccount.Response unregisterAccount(
            @RequestBody @Valid DeleteAccount.Request request) {

        Account account =
                accountService.unregisterAccount(
                        request.getUserId(), request.getAccountNumber());

        return DeleteAccount.Response.of(account, request.getUserId());
    }
}
