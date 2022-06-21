package com.dokuny.accountmanagement.aop;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AccountLock {
    long tryLockTime() default 5000L;
}
