package com.sudhakar.banking.controller;
import com.sudhakar.banking.dto.BankDtos.*;
import com.sudhakar.banking.entity.AccountStatus;
import com.sudhakar.banking.service.BankingService;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final BankingService service;public AdminController(BankingService s){service=s;}
 @GetMapping("/users") public List<UserResponse> users(){return service.allUsers();}
 @PatchMapping("/users/{id}/enable") public UserResponse enable(@PathVariable Long id){return service.setUserEnabled(id,true);}
 @PatchMapping("/users/{id}/disable") public UserResponse disable(@PathVariable Long id){return service.setUserEnabled(id,false);}
 @PatchMapping("/accounts/{number}/block") public AccountResponse block(@PathVariable String number){return service.setAccountStatus(number,AccountStatus.BLOCKED);}
 @PatchMapping("/accounts/{number}/unblock") public AccountResponse unblock(@PathVariable String number){return service.setAccountStatus(number,AccountStatus.ACTIVE);}
}
