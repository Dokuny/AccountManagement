package com.dokuny.accountmanagement.domain;

import com.dokuny.accountmanagement.domain.util.BaseTimeEntity;
import com.dokuny.accountmanagement.type.AccountStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private AccountUser accountUser;

    @Column(unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private Long balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime unregisteredAt;


    public void close() {
        this.unregisteredAt = LocalDateTime.now();
        this.accountStatus = AccountStatus.CLOSED;
    }
}
