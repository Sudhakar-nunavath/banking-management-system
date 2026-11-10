package com.sudhakar.banking.exception;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(ApiException.class) public ResponseEntity<?> api(ApiException e){return response(e.getStatus(),e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> validation(MethodArgumentNotValidException e){
  Map<String,String> errors=new LinkedHashMap<>(); e.getBindingResult().getFieldErrors().forEach(x->errors.put(x.getField(),x.getDefaultMessage()));
  return ResponseEntity.badRequest().body(Map.of("timestamp",LocalDateTime.now(),"status",400,"error","Validation failed","details",errors));
 }
 @ExceptionHandler(AccessDeniedException.class) public ResponseEntity<?> denied(Exception e){return response(HttpStatus.FORBIDDEN,"You are not allowed to perform this action");}
 @ExceptionHandler(Exception.class) public ResponseEntity<?> other(Exception e){return response(HttpStatus.INTERNAL_SERVER_ERROR,"Unexpected server error");}
 private ResponseEntity<?> response(HttpStatus s,String m){return ResponseEntity.status(s).body(Map.of("timestamp",LocalDateTime.now(),"status",s.value(),"error",s.getReasonPhrase(),"message",m));}
}
