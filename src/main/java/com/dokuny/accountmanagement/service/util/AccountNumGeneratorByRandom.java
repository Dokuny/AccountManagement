package com.dokuny.accountmanagement.service.util;

import org.springframework.stereotype.Component;

import java.util.Random;


@Component
public class AccountNumGeneratorByRandom implements AccountNumGenerator{

    private Random random = new Random();
    @Override
    public String generateNumber() {
        StringBuilder sb = new StringBuilder();

        int cnt = 0;
        while (cnt < 10) {
            if (cnt == 0) {
                sb.append(random.nextInt(8) + 1);
            } else {
                sb.append(random.nextInt(9));
            }
            cnt++;
        }

        return sb.toString();
    }
}
