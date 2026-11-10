package com.sudhakar.banking.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class BankDtos {
 public record AccountResponse(Long id,String accountNumber,BigDecimal balance,String currency,String status,LocalDateTime createdAt) {}
 public record MoneyRequest(@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal amount,@Size(max=200) String description) {}
 public record TransferRequest(@NotBlank String fromAccount,@NotBlank String toAccount,@NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) BigDecimal amount,@Size(max=200) String description) {}
 public record TransactionResponse(String reference,String type,BigDecimal amount,BigDecimal balanceAfter,String description,String counterpartyAccount,LocalDateTime createdAt) {}
 public record UserResponse(Long id,String fullName,String email,String role,boolean enabled,LocalDateTime createdAt) {}
}
