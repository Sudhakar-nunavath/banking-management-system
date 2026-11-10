package com.sudhakar.banking.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="bank_accounts", indexes=@Index(name="idx_account_owner", columnList="owner_id"))
public class BankAccount {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true, length=24) private String accountNumber;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="owner_id") private User owner;
 @Column(nullable=false, precision=19, scale=2) private BigDecimal balance=BigDecimal.ZERO;
 @Column(nullable=false, length=3) private String currency="INR";
 @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountStatus status=AccountStatus.ACTIVE;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;} public String getAccountNumber(){return accountNumber;} public void setAccountNumber(String v){accountNumber=v;}
 public User getOwner(){return owner;} public void setOwner(User v){owner=v;}
 public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
 public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
 public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus v){status=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
