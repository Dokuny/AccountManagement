package com.dokuny.accountmanagement.controller;

import com.dokuny.accountmanagement.dto.CancelTransaction;
import com.dokuny.accountmanagement.dto.CheckTransaction;
import com.dokuny.accountmanagement.dto.UseBalanceTransaction;

import com.dokuny.accountmanagement.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RequiredArgsConstructor
@RequestMapping("/transaction")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/use")
    public UseBalanceTransaction.Response useBalanceByTransaction(
            @RequestBody @Valid UseBalanceTransaction.Request request) {
        return UseBalanceTransaction.Response.of(
                transactionService.useBalance(
                        request.getUserId(), request.getAccountNumber(), request.getAmount()));
    }

    @PostMapping("/cancel")
    public CancelTransaction.Response cancelTransaction(
            @RequestBody @Valid CancelTransaction.Request request) {



        return CancelTransaction.Response.of(transactionService
                .cancelTransaction(
                        request.getTransactionId(),
                        request.getAccountNumber(),
                        request.getAmount()));
    }


    @GetMapping("/{transactionId}")
    public CheckTransaction.Response checkTransaction(@PathVariable String transactionId) {
        return CheckTransaction.Response.of(transactionService.checkTransaction(transactionId));
    }
}
