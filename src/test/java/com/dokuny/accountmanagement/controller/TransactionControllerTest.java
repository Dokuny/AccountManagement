package com.dokuny.accountmanagement.controller;


import com.dokuny.accountmanagement.domain.Account;
import com.dokuny.accountmanagement.domain.Transaction;
import com.dokuny.accountmanagement.dto.CancelTransaction;
import com.dokuny.accountmanagement.dto.TransactionDto;
import com.dokuny.accountmanagement.dto.UseBalanceTransaction;
import com.dokuny.accountmanagement.exception.TransactionException;

import com.dokuny.accountmanagement.service.TransactionService;

import com.dokuny.accountmanagement.type.ErrorCode;
import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;


    @Test
    @DisplayName("잔액 사용 성공")
    void useBalanceByTransactionSuccess() throws Exception {
        //given

        LocalDateTime time = LocalDateTime.now();
        UseBalanceTransaction.Request request =
                UseBalanceTransaction.Request.builder()
                        .accountNumber("1234567890")
                        .userId(10L)
                        .amount(500L)
                        .build();

        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .transactionType(TransactionType.USE)
                        .transactionId("test")
                        .accountNumber("1234567890")
                        .amount(500L)
                        .transactedAt(time)
                        .transactionResultStatus(TransactionResultStatus.SUCCESS)
                        .build());

        //when
        //then
        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.transactionResultStatus")
                        .value(TransactionResultStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionId")
                        .value("test"))
                .andExpect(jsonPath("$.amount").
                        value(500L))
                .andExpect(jsonPath("$.transactedAt")
                        .value(time.format(DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잔액 사용 실패")
    void useBalanceByTransactionFail() throws Exception {
        //given
        TransactionException ex =
                new TransactionException(ErrorCode.NO_SUCH_ACCOUNT);

        UseBalanceTransaction.Request request =
                UseBalanceTransaction.Request.builder()
                        .accountNumber("1234567890")
                        .userId(10L)
                        .amount(500L)
                        .build();

        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willThrow(ex);

        //when
        //then
        mockMvc.perform(post("/transaction/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errorCode")
                        .value(ex.getErrorCode().toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ex.getErrorMessage()))
                .andExpect(jsonPath("$.timesStamp")
                        .value(ex.getTimesStamp()
                                .format(DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))));

    }

    @Test
    @DisplayName("거래 취소 성공")
    void cancelTransaction() throws Exception {
        //given
        LocalDateTime time = LocalDateTime.now();
        CancelTransaction.Request request =
                CancelTransaction.Request.builder()
                        .accountNumber("1234567890")
                        .transactionId("test")
                        .amount(500L)
                        .build();

        given(transactionService.cancelTransaction(anyString(), anyString(), anyLong()))
                .willReturn(TransactionDto.builder()
                        .transactionId("test")
                        .transactionType(TransactionType.CANCEL)
                        .accountNumber("1234567890")
                        .amount(500L)
                        .transactedAt(time)
                        .transactionResultStatus(TransactionResultStatus.SUCCESS)
                        .build());

        //when
        //then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.transactionResultStatus")
                        .value(TransactionResultStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionId")
                        .value("test"))
                .andExpect(jsonPath("$.amount").
                        value(500L))
                .andExpect(jsonPath("$.transactedAt")
                        .value(time.format(DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("거래 취소 실패")
    void cancelTransactionFail() throws Exception {
        //given
        TransactionException ex =
                new TransactionException(ErrorCode.TRANSACTION_NOT_EXIST);

        CancelTransaction.Request request =
                CancelTransaction.Request.builder()
                        .accountNumber("1234567890")
                        .transactionId("test")
                        .amount(500L)
                        .build();

        given(transactionService.cancelTransaction(anyString(), anyString(), anyLong()))
                .willThrow(ex);

        //when
        //then
        mockMvc.perform(post("/transaction/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errorCode")
                        .value(ex.getErrorCode().toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ex.getErrorMessage()))
                .andExpect(jsonPath("$.timesStamp")
                        .value(ex.getTimesStamp()
                                .format(DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))));

    }

    @Test
    @DisplayName("거래 조회")
    void checkTransaction() throws Exception {
        //given
        LocalDateTime time = LocalDateTime.now();
        given(transactionService.checkTransaction(anyString()))
                .willReturn(TransactionDto.builder()
                        .transactionId("test")
                        .transactionType(TransactionType.USE)
                        .accountNumber("1234567890")
                        .amount(500L)
                        .transactedAt(time)
                        .transactionResultStatus(TransactionResultStatus.SUCCESS)
                        .build());

        //when
        //then
        mockMvc.perform(get("/transaction/test"))
                .andDo(print())
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.transactionType")
                        .value(TransactionType.USE.toString()))
                .andExpect(jsonPath("$.transactionResultStatus")
                        .value(TransactionResultStatus.SUCCESS.toString()))
                .andExpect(jsonPath("$.transactionId")
                        .value("test"))
                .andExpect(jsonPath("$.amount")
                        .value(500))
                .andExpect(jsonPath("$.transactedAt")
                        .value(time
                                .format(DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("거래 조회 실패")
    void checkTransactionFail() throws Exception {
        //given
        TransactionException ex =
                new TransactionException(ErrorCode.TRANSACTION_NOT_EXIST);

        given(transactionService.checkTransaction(anyString()))
                .willThrow(ex);

        //when
        //then
        mockMvc.perform(get("/transaction/test"))
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errorCode")
                        .value(ex.getErrorCode().toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ex.getErrorMessage()))
                .andExpect(jsonPath("$.timesStamp")
                        .value(ex.getTimesStamp()
                                .format(DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))));

    }
}