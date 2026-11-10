package com.sudhakar.banking.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="bank_transactions", indexes={
 @Index(name="idx_tx_account_created", columnList="account_id, createdAt"),
 @Index(name="idx_tx_reference", columnList="reference", unique=true)})
public class BankTransaction {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false, unique=true) private String reference;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="account_id") private BankAccount account;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private TransactionType type;
 @Column(nullable=false, precision=19, scale=2) private BigDecimal amount;
 @Column(nullable=false, precision=19, scale=2) private BigDecimal balanceAfter;
 private String description;
 private String counterpartyAccount;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;} public String getReference(){return reference;} public void setReference(String v){reference=v;}
 public BankAccount getAccount(){return account;} public void setAccount(BankAccount v){account=v;}
 public TransactionType getType(){return type;} public void setType(TransactionType v){type=v;}
 public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
 public BigDecimal getBalanceAfter(){return balanceAfter;} public void setBalanceAfter(BigDecimal v){balanceAfter=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getCounterpartyAccount(){return counterpartyAccount;} public void setCounterpartyAccount(String v){counterpartyAccount=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
