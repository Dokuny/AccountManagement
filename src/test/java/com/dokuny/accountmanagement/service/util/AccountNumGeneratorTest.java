package com.dokuny.accountmanagement.service.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AccountNumGeneratorTest {

    @Autowired
    private AccountNumGenerator accountNumGenerator;

    @Test
    void generateNumber(){
        //given
        String number = accountNumGenerator.generateNumber();
        //when
        //then
        assertEquals(10,number.length());
    }

}