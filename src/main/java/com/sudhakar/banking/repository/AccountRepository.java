package com.sudhakar.banking.repository;
import com.sudhakar.banking.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface AccountRepository extends JpaRepository<BankAccount,Long> {
 List<BankAccount> findByOwnerId(Long ownerId);
 Optional<BankAccount> findByAccountNumber(String accountNumber);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select a from BankAccount a where a.accountNumber=:number")
 Optional<BankAccount> findByAccountNumberForUpdate(@Param("number") String number);
}
