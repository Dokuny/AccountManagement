package com.dokuny.accountmanagement.domain;

import com.dokuny.accountmanagement.domain.util.BaseTimeEntity;

import com.dokuny.accountmanagement.type.TransactionResultStatus;
import com.dokuny.accountmanagement.type.TransactionType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Transaction extends BaseTimeEntity {

    @Id
    @GenericGenerator(name = "myGen",strategy = "com.dokuny.accountmanagement.domain.util.NoDashUUIDGenerator")
    @GeneratedValue(generator = "myGen")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionResultStatus transactionResultStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceSnapShot;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime transactedAt;

}
