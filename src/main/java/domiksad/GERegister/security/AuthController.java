package domiksad.GERegister.security;

import domiksad.GERegister.security.dto.JwtResponse;
import domiksad.GERegister.security.dto.LoginRequest;
import domiksad.GERegister.security.dto.SignupRequest;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtil.generateToken(authentication.getName());
    return ResponseEntity.ok(new JwtResponse(jwt));
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.username())) {
      return ResponseEntity.badRequest().body("Error: Username is already taken!");
    }

    User user = new User();
    user.setUsername(signUpRequest.username());
    user.setPassword(encoder.encode(signUpRequest.password())); // Hashowanie haseł
    user.setRoles(Set.of(Role.HUNTER)); // Domyślna rola

    userRepository.save(user);
    return ResponseEntity.ok("User registered successfully!");
  }
}
