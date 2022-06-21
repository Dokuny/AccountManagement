package com.dokuny.accountmanagement.controller;

import com.dokuny.accountmanagement.dto.CreateAccount;
import com.dokuny.accountmanagement.dto.DeleteAccount;
import com.dokuny.accountmanagement.dto.GetAccount;
import com.dokuny.accountmanagement.service.AccountService;
import com.dokuny.accountmanagement.aop.AccountLock;
import com.dokuny.accountmanagement.aop.UserLock;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    @UserLock
    @PostMapping
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {
        return CreateAccount.Response.of(
                accountService
                        .createAccount(request.getUserId(), request.getInitialBalance()));
    }

    @GetMapping
    public List<GetAccount.Response> getAccountAll(@RequestParam("userId") Long userId) {
        return GetAccount.Response.of(accountService.getAccountAll(userId));
    }

    @AccountLock
    @DeleteMapping
    public DeleteAccount.Response unregisterAccount(
            @RequestBody @Valid DeleteAccount.Request request) {

        return DeleteAccount.Response.of(
                accountService.unregisterAccount(request.getUserId(), request.getAccountNumber()));
    }
}
