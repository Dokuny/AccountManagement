package com.dokuny.accountmanagement.controller;

import com.dokuny.accountmanagement.domain.Transaction;
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

        Transaction transaction = transactionService.useBalance(
                request.getUserId(), request.getAccountNumber(), request.getAmount());

        return UseBalanceTransaction.Response.of(
                transaction, request.getAccountNumber(), request.getAmount());
    }

    @PostMapping("/cancel")
    public CancelTransaction.Response cancelTransaction(
            @RequestBody @Valid CancelTransaction.Request request) {

        Transaction transaction =
                transactionService
                        .cancelTransaction(
                                request.getTransactionId(),
                                request.getAccountNumber(),
                                request.getAmount());

        return CancelTransaction.Response.of(transaction, request.getAccountNumber());
    }



    @GetMapping("/{transactionId}")
    public CheckTransaction.Response checkTransaction(@PathVariable String transactionId) {

        Transaction transaction =
                transactionService.checkTransaction(transactionId);

        return CheckTransaction.Response.of(transaction);
    }
}
