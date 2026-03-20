package domiksad.GERegister.security;

import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpeditionSecurity {
  private final ExpeditionRepository expeditionRepository;
  private final UserRepository userRepository;

  public boolean isHunterInExpedition(UUID id, String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username))
        .getHunter()
        .getExpeditions()
        .stream()
        .anyMatch(ee -> ee.getId().equals(id));
  }
}
