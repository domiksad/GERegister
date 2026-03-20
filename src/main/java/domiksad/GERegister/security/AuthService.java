package domiksad.GERegister.security;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.security.dto.JwtResponse;
import domiksad.GERegister.security.dto.LoginRequest;
import domiksad.GERegister.security.dto.RegisterRequest;
import domiksad.GERegister.security.dto.SignupRequest;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  private final HunterRepository hunterRepository;

  @Value("${jwt.issuer}")
  private String issuer;

  @Value("${jwt.expiration}")
  private long expirationMs;

  public JwtResponse login(LoginRequest request) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.username(),
            request.password()
        )
    );
    User user = (User) authentication.getPrincipal();
    Instant now = Instant.now();
    String roles = user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(" "));
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(issuer)
        .subject(user.getUsername())
        .issuedAt(now)
        .expiresAt(now.plusMillis(expirationMs))
        .claim("roles", roles)
        .build();
    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
    String token = jwtEncoder.encode(JwtEncoderParameters.from(header,
        claims)).getTokenValue();
    return new JwtResponse(token);
  }

  /* example use
  {
    "signupData": {
        "username": "tester",
        "password": "password123"
    },
    "role": "ADMIN"
  }
  {
    "signupData": {
      "username": "tester2",
      "password": "password123"
    },
    "role": "HUNTER",
    "hunterId": "3b5e97da-a850-4d8f-b2fb-4ebc5ce29f69"
  }
  */
  public String register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.signupData().username())) {
      throw new IllegalArgumentException("Username is already taken");
    }
    User user = new User();
    user.setUsername(request.signupData().username());
    user.setPassword(passwordEncoder.encode(request.signupData().password()));
    user.setRoles(Set.of(request.role()));

    if(request.hunterId() != null){
      user.setHunter(hunterRepository.findById(request.hunterId()).orElseThrow(
          () -> new HunterNotFoundException(request.hunterId())
      ));
    }

    userRepository.save(user);
    return "User registered successfully";
  }
}
