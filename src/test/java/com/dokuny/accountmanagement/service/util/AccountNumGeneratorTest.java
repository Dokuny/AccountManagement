package com.dokuny.accountmanagement.service.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


class AccountNumGeneratorTest {

    private final AccountNumGenerator accountNumGenerator
            = new AccountNumGeneratorByRandom();

    @Test
    @DisplayName("계좌번호 생성")
    void generateNumber() {
        //given
        String number = accountNumGenerator.generateNumber();
        //when
        //then
        assertEquals(10, number.length());
    }

}