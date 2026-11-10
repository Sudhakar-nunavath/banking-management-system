package com.sudhakar.banking.config;
import com.sudhakar.banking.entity.Role;
import com.sudhakar.banking.entity.User;
import com.sudhakar.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
/** Creates a local development admin only when explicitly enabled. */
@Configuration
public class DevAdminInitializer {
 @Bean CommandLineRunner createAdmin(UserRepository users,PasswordEncoder encoder,
   @Value("${app.dev-admin.enabled:false}") boolean enabled,
   @Value("${app.dev-admin.email:admin@bank.local}") String email,
   @Value("${app.dev-admin.password:ChangeMe123!}") String password){
  return args->{if(enabled&&!users.existsByEmail(email)){
   User u=new User();u.setFullName("Local Admin");u.setEmail(email);u.setPassword(encoder.encode(password));u.setRole(Role.ADMIN);users.save(u);
  }};
 }
}
