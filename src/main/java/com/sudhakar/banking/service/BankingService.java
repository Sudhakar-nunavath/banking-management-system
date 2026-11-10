package com.sudhakar.banking.service;
import com.sudhakar.banking.dto.BankDtos.*;
import com.sudhakar.banking.entity.*;
import com.sudhakar.banking.exception.ApiException;
import com.sudhakar.banking.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.util.*;
@Service
public class BankingService {
 private final AccountRepository accounts; private final UserRepository users; private final TransactionRepository txs;
 public BankingService(AccountRepository a,UserRepository u,TransactionRepository t){accounts=a;users=u;txs=t;}
 @Transactional public AccountResponse openAccount(String email){
  User owner=user(email); BankAccount a=new BankAccount();a.setOwner(owner);a.setAccountNumber("KRW"+UUID.randomUUID().toString().replace("-","").substring(0,13).toUpperCase());
  return account(accounts.save(a));
 }
 @Transactional(readOnly=true) public List<AccountResponse> myAccounts(String email){return accounts.findByOwnerId(user(email).getId()).stream().map(this::account).toList();}
 @Transactional(readOnly=true) public AccountResponse accountDetails(String email,String number){return account(owned(email,number));}
 @Transactional public AccountResponse deposit(String email,String number,MoneyRequest r){
  BankAccount a=lockedOwned(email,number);active(a);BigDecimal amount=money(r.amount());a.setBalance(a.getBalance().add(amount));
  record(a,TransactionType.DEPOSIT,amount,r.description(),null);return account(a);
 }
 @Transactional public AccountResponse withdraw(String email,String number,MoneyRequest r){
  BankAccount a=lockedOwned(email,number);active(a);BigDecimal amount=money(r.amount());
  if(a.getBalance().compareTo(amount)<0)throw new ApiException(HttpStatus.BAD_REQUEST,"Insufficient balance");
  a.setBalance(a.getBalance().subtract(amount));record(a,TransactionType.WITHDRAWAL,amount,r.description(),null);return account(a);
 }
 @Transactional public Map<String,Object> transfer(String email,TransferRequest r){
  if(r.fromAccount().equals(r.toAccount()))throw new ApiException(HttpStatus.BAD_REQUEST,"Source and destination accounts must differ");
  // Deterministic lock order reduces deadlock risk for concurrent opposite transfers.
  String first=r.fromAccount().compareTo(r.toAccount())<0?r.fromAccount():r.toAccount();
  String second=first.equals(r.fromAccount())?r.toAccount():r.fromAccount();
  BankAccount one=accounts.findByAccountNumberForUpdate(first).orElseThrow(()->notFound("Account not found"));
  BankAccount two=accounts.findByAccountNumberForUpdate(second).orElseThrow(()->notFound("Account not found"));
  BankAccount from=one.getAccountNumber().equals(r.fromAccount())?one:two;
  BankAccount to=one.getAccountNumber().equals(r.toAccount())?one:two;
  if(!from.getOwner().getEmail().equalsIgnoreCase(email))throw new ApiException(HttpStatus.FORBIDDEN,"You do not own the source account");
  active(from);active(to);BigDecimal amount=money(r.amount());
  if(from.getBalance().compareTo(amount)<0)throw new ApiException(HttpStatus.BAD_REQUEST,"Insufficient balance");
  from.setBalance(from.getBalance().subtract(amount));to.setBalance(to.getBalance().add(amount));
  String ref=UUID.randomUUID().toString();
  record(from,TransactionType.TRANSFER_OUT,amount,r.description(),to.getAccountNumber(),ref);
  record(to,TransactionType.TRANSFER_IN,amount,r.description(),from.getAccountNumber(),ref+"-IN");
  return Map.of("message","Transfer completed","reference",ref,"from",account(from),"to",account(to));
 }
 @Transactional(readOnly=true) public Page<TransactionResponse> history(String email,String number,int page,int size){
  BankAccount a=owned(email,number);Pageable p=PageRequest.of(Math.max(0,page),Math.min(Math.max(1,size),100),Sort.by("createdAt").descending());
  return txs.findByAccountIdOrderByCreatedAtDesc(a.getId(),p).map(t->new TransactionResponse(t.getReference(),t.getType().name(),t.getAmount(),t.getBalanceAfter(),t.getDescription(),t.getCounterpartyAccount(),t.getCreatedAt()));
 }
 @Transactional(readOnly=true) public List<UserResponse> allUsers(){return users.findAll().stream().map(u->new UserResponse(u.getId(),u.getFullName(),u.getEmail(),u.getRole().name(),u.isEnabled(),u.getCreatedAt())).toList();}
 @Transactional public AccountResponse setAccountStatus(String number,AccountStatus status){
  BankAccount a=accounts.findByAccountNumberForUpdate(number).orElseThrow(()->notFound("Account not found"));a.setStatus(status);return account(a);
 }
 @Transactional public UserResponse setUserEnabled(Long id,boolean enabled){
  User u=users.findById(id).orElseThrow(()->notFound("User not found"));u.setEnabled(enabled);
  return new UserResponse(u.getId(),u.getFullName(),u.getEmail(),u.getRole().name(),u.isEnabled(),u.getCreatedAt());
 }
 private User user(String email){return users.findByEmail(email).orElseThrow(()->notFound("User not found"));}
 private BankAccount owned(String email,String number){BankAccount a=accounts.findByAccountNumber(number).orElseThrow(()->notFound("Account not found"));if(!a.getOwner().getEmail().equalsIgnoreCase(email))throw new ApiException(HttpStatus.FORBIDDEN,"You do not own this account");return a;}
 private BankAccount lockedOwned(String email,String number){BankAccount a=accounts.findByAccountNumberForUpdate(number).orElseThrow(()->notFound("Account not found"));if(!a.getOwner().getEmail().equalsIgnoreCase(email))throw new ApiException(HttpStatus.FORBIDDEN,"You do not own this account");return a;}
 private void active(BankAccount a){if(a.getStatus()!=AccountStatus.ACTIVE)throw new ApiException(HttpStatus.BAD_REQUEST,"Account is not active");}
 private BigDecimal money(BigDecimal n){if(n==null||n.scale()>2||n.compareTo(BigDecimal.ZERO)<=0)throw new ApiException(HttpStatus.BAD_REQUEST,"Amount must be positive and have at most two decimal places");return n.setScale(2,RoundingMode.UNNECESSARY);}
 private void record(BankAccount a,TransactionType type,BigDecimal amount,String description,String counterparty){record(a,type,amount,description,counterparty,UUID.randomUUID().toString());}
 private void record(BankAccount a,TransactionType type,BigDecimal amount,String description,String counterparty,String ref){
  BankTransaction t=new BankTransaction();t.setReference(ref);t.setAccount(a);t.setType(type);t.setAmount(amount);t.setBalanceAfter(a.getBalance());t.setDescription(description);t.setCounterpartyAccount(counterparty);txs.save(t);
 }
 private AccountResponse account(BankAccount a){return new AccountResponse(a.getId(),a.getAccountNumber(),a.getBalance(),a.getCurrency(),a.getStatus().name(),a.getCreatedAt());}
 private ApiException notFound(String m){return new ApiException(HttpStatus.NOT_FOUND,m);}
}
