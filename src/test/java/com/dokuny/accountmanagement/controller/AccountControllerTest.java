package com.dokuny.accountmanagement.controller;

import com.dokuny.accountmanagement.dto.AccountDto;
import com.dokuny.accountmanagement.dto.CreateAccount;
import com.dokuny.accountmanagement.dto.DeleteAccount;
import com.dokuny.accountmanagement.exception.AccountException;
import com.dokuny.accountmanagement.service.AccountService;
import com.dokuny.accountmanagement.type.ErrorCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// 테스트하기위한 컨트롤러를 명시, MockMvc를 주입해줍니다.
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;


    /**
     * CreateAccount Test
     */

    @Test
    @DisplayName("계좌 생성 성공")
    void createAccountSuccess() throws Exception {
        //given
        LocalDateTime time = LocalDateTime.now();

        given(accountService.createAccount(10L, 500L))
                .willReturn(AccountDto.builder()
                        .userId(10L)
                        .balance(500L)
                        .accountNumber("1234567890")
                        .registeredAt(time)
                        .build());

        CreateAccount.Request request = CreateAccount.Request.builder()
                .userId(10L)
                .initialBalance(500L)
                .build();

        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.registeredAt")
                        .value(time.format(DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계좌 생성 실패")
    void createAccountFail() throws Exception {
        //given

        AccountException exception = new AccountException(ErrorCode.USER_NOT_EXIST);
        given(accountService.createAccount(anyLong(), anyLong()))
                .willThrow(exception);

        CreateAccount.Request request = CreateAccount.Request.builder()
                .userId(11L)
                .initialBalance(500L)
                .build();

        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status")
                        .value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.errorCode")
                        .value(exception.getErrorCode().toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(exception.getErrorMessage()))
                .andExpect(jsonPath("$.timesStamp")
                        .value(exception.getTimesStamp()
                                .format(DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))));
    }


    /**
     * getAccount Controller Test
     */

    @Test
    @DisplayName("사용자의 모든 계좌 조회 성공")
    void getAccountAllSuccess() throws Exception {
        //given
        List<AccountDto> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            list.add(AccountDto.builder()
                    .accountNumber("1234567890")
                    .balance(500L)
                    .build());
        }

        given(accountService.getAccountAll(anyLong()))
                .willReturn(list);

        //when
        //then
        mockMvc.perform(get("/account")
                        .queryParam("userId","1234"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$[0].balance")
                        .value(500))
                .andExpect(jsonPath("$[1].accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$[1].balance")
                        .value(500))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자의 모든 계좌 조회 실패")
    void getAccountAllFail() throws Exception {
        //given
        AccountException ex = new AccountException(ErrorCode.USER_NOT_EXIST);
        given(accountService.getAccountAll(anyLong()))
                .willThrow(ex);
        //when
        //then
        mockMvc.perform(get("/account")
                        .queryParam("userId","1234"))
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

    /**
     * unregisterAccount Controller Test
     */

    @Test
    @DisplayName("계좌 해지 성공")
    void unregisterAccountAll() throws Exception {
        LocalDateTime time = LocalDateTime.now();
        //given
        given(accountService.unregisterAccount(10L, "1234567890"))
                .willReturn(AccountDto.builder()
                        .unregisteredAt(time)
                        .accountNumber("1234567890")
                        .userId(10L)
                        .build());

        DeleteAccount.Request request = DeleteAccount.Request.builder()
                .userId(10L)
                .accountNumber("1234567890")
                .build();

        //when
        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.accountNumber")
                        .value("1234567890"))
                .andExpect(jsonPath("$.unregisteredAt")
                        .value(time.format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("계좌 해지 실패")
    void unregisterAccountFail() throws Exception {

        AccountException ex = new AccountException(ErrorCode.ACCOUNT_REMAINED_BALANCE);
        //given
        given(accountService.unregisterAccount(10L, "1234567890"))
                .willThrow(ex);

        DeleteAccount.Request request = DeleteAccount.Request.builder()
                .userId(10L)
                .accountNumber("1234567890")
                .build();
        //when
        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.errorCode").value(ex.getErrorCode().toString()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ex.getErrorMessage()))
                .andExpect(jsonPath("$.timesStamp")
                        .value(ex.getTimesStamp().format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS"))))
                .andExpect(status().isBadRequest());
    }


}