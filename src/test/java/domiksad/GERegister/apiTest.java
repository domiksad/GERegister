package domiksad.GERegister;

import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.security.AuthService;
import domiksad.GERegister.security.dto.RegisterRequest;
import domiksad.GERegister.security.dto.SignupRequest;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class apiTest {
  @Autowired private ExpeditionRepository expeditionRepository;
  @Autowired private HunterRepository hunterRepository;
  @Autowired private UserRepository userRepository;

  @Autowired private AuthService authService;

  @BeforeEach
  void clean() {
    expeditionRepository.deleteAll();
    hunterRepository.deleteAll();
    userRepository.deleteAll();
  }

  private User getHunterUser(UUID hunterId) {
    authService.register(
        new RegisterRequest(new SignupRequest("ABC", "DEF"), Role.HUNTER, hunterId));
    return userRepository
        .findByUsername("ABC")
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  private User getAdminUser() {
    authService.register(new RegisterRequest(new SignupRequest("DEF", "DEF"), Role.ADMIN, null));
    return userRepository
        .findByUsername("ABC")
        .orElseThrow(() -> new RuntimeException("User not found"));
  }
}
