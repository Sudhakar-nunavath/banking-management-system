package com.sudhakar.banking.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="app_users")
public class User {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String fullName;
 @Column(nullable=false, unique=true) private String email;
 @Column(nullable=false) private String password;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.CUSTOMER;
 @Column(nullable=false) private boolean enabled=true;
 @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
 public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public Role getRole(){return role;} public void setRole(Role v){role=v;}
 public boolean isEnabled(){return enabled;} public void setEnabled(boolean v){enabled=v;}
 public LocalDateTime getCreatedAt(){return createdAt;}
}
