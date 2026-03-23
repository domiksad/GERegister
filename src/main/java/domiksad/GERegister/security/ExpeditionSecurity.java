package domiksad.GERegister.security;

import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExpeditionSecurity {
  private final ExpeditionRepository expeditionRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public boolean isHunterInExpedition(UUID expeditionId, Authentication authentication) {
    if (authentication.getPrincipal() instanceof User user) {
      return userRepository.findById(user.getId())
          .map(User::getHunter)
          .filter(hunter -> hunter != null)
          .map(hunter -> hunter.getExpeditions().stream()
              .anyMatch(expedition -> expedition.getId().equals(expeditionId))
          )
          .orElse(false);
    }
    return false;
  }

  @Transactional(readOnly = true)
  public boolean isMyHunterProfile(UUID hunterId, Authentication authentication) {
    if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
      return false;
    }

    return userRepository.findByUsername(user.getUsername())
        .map(u -> u.getHunter() != null && u.getHunter().getId().equals(hunterId))
        .orElse(false);
  }
}
