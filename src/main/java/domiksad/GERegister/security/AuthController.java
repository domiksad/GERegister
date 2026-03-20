package domiksad.GERegister.security;

import domiksad.GERegister.security.dto.JwtResponse;
import domiksad.GERegister.security.dto.LoginRequest;
import domiksad.GERegister.security.dto.RegisterRequest;
import domiksad.GERegister.security.dto.SignupRequest;
import domiksad.GERegister.security.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(Authentication authentication) {
    return ResponseEntity.ok(Map.of(
        "username", authentication.getName(),
        "roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList()
    ));
  }
}
