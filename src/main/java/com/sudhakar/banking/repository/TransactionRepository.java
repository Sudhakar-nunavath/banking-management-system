package com.sudhakar.banking.repository;
import com.sudhakar.banking.entity.BankTransaction;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransactionRepository extends JpaRepository<BankTransaction,Long> {
 Page<BankTransaction> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);
}
