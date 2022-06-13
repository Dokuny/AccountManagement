package com.dokuny.accountmanagement.config.policy;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:policy.properties")
@ConfigurationProperties(prefix = "policy.account")
@Getter @Setter
public class PolicyAccountProperties {
    private int max;
}
