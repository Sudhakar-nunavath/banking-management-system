package com.sudhakar.banking.controller;
import com.sudhakar.banking.dto.BankDtos.*;
import com.sudhakar.banking.service.BankingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/accounts")
public class AccountController {
 private final BankingService service;public AccountController(BankingService s){service=s;}
 @PostMapping public AccountResponse open(Authentication a){return service.openAccount(a.getName());}
 @GetMapping public List<AccountResponse> mine(Authentication a){return service.myAccounts(a.getName());}
 @GetMapping("/{number}") public AccountResponse details(Authentication a,@PathVariable String number){return service.accountDetails(a.getName(),number);}
 @PostMapping("/{number}/deposit") public AccountResponse deposit(Authentication a,@PathVariable String number,@Valid @RequestBody MoneyRequest r){return service.deposit(a.getName(),number,r);}
 @PostMapping("/{number}/withdraw") public AccountResponse withdraw(Authentication a,@PathVariable String number,@Valid @RequestBody MoneyRequest r){return service.withdraw(a.getName(),number,r);}
 @PostMapping("/transfer") public Map<String,Object> transfer(Authentication a,@Valid @RequestBody TransferRequest r){return service.transfer(a.getName(),r);}
 @GetMapping("/{number}/transactions") public Page<TransactionResponse> history(Authentication a,@PathVariable String number,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.history(a.getName(),number,page,size);}
}
