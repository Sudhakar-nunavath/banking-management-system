package com.sudhakar.banking.service;
import com.sudhakar.banking.dto.AuthDtos.*;
import com.sudhakar.banking.entity.*;
import com.sudhakar.banking.exception.ApiException;
import com.sudhakar.banking.repository.UserRepository;
import com.sudhakar.banking.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
 private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager auth; private final JwtService jwt;
 public AuthService(UserRepository u,PasswordEncoder e,AuthenticationManager a,JwtService j){users=u;encoder=e;auth=a;jwt=j;}
 @Transactional public AuthResponse register(RegisterRequest r){
  String email=r.email().trim().toLowerCase();
  if(users.existsByEmail(email))throw new ApiException(HttpStatus.CONFLICT,"Email is already registered");
  User u=new User();u.setFullName(r.fullName().trim());u.setEmail(email);u.setPassword(encoder.encode(r.password()));u.setRole(Role.CUSTOMER);users.save(u);
  return response(u);
 }
 public AuthResponse login(LoginRequest r){
  String email=r.email().trim().toLowerCase();
  try{auth.authenticate(new UsernamePasswordAuthenticationToken(email,r.password()));}
  catch(AuthenticationException e){throw new ApiException(HttpStatus.UNAUTHORIZED,"Invalid email or password");}
  return response(users.findByEmail(email).orElseThrow());
 }
 private AuthResponse response(User u){return new AuthResponse(jwt.generate(u.getEmail()),"Bearer",u.getId(),u.getFullName(),u.getEmail(),u.getRole().name());}
}
