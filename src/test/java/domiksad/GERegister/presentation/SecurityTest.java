package domiksad.GERegister.presentation;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.security.AuthService;
import domiksad.GERegister.security.dto.RegisterRequest;
import domiksad.GERegister.security.dto.SignupRequest;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTest {
  // TODO: Fix disabled tests

  @Autowired private MockMvc mockMvc;
  @Autowired private HunterRepository hunterRepository;
  @Autowired private AuthService authService;
  @Autowired private UserRepository userRepository;
  @Autowired private ExpeditionRepository expeditionRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    hunterRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void notLoggedInUser_throwsUnauthorized() throws Exception {
    mockMvc.perform(get("/api/hunters")).andExpect(status().isUnauthorized());
  }

  @Test
  void hunterCreateExpedition_throwsForbidden() throws Exception {
    String req =
        """
            {
              "name": "abc",
              "reward": "def",
              "difficulty": "EASY"
            }
            """;

    mockMvc
        .perform(
            post("/api/expeditions")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_HUNTER")))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isForbidden());
  }

  @Test
  @Disabled
  void hunterAccessNotHisData_throwsForbidden() throws Exception {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();

    HunterEntity h1 = new HunterEntity(id1, "abc", new HashSet<>());
    HunterEntity h2 = new HunterEntity(id2, "def", new HashSet<>());

    hunterRepository.saveAndFlush(h1);
    hunterRepository.saveAndFlush(h2);

    authService.register(new RegisterRequest(new SignupRequest("ABC", "DEF"), Role.HUNTER, id1));

    User userPrincipal =
        userRepository
            .findByUsername("ABC")
            .orElseThrow(() -> new RuntimeException("User not found"));

    mockMvc
        .perform(get("/api/hunters/%s".formatted(id2)).with(user(userPrincipal)))
        .andExpect(status().isForbidden());
  }

  @Test
  void commanderCreatesExpedition() throws Exception {
    authService.register(
        new RegisterRequest(new SignupRequest("ABC", "DEF"), Role.COMMANDER, null));

    User userPrincipal =
        userRepository
            .findByUsername("ABC")
            .orElseThrow(() -> new RuntimeException("User not found"));

    String req =
        """
        {
          "name": "TEST",
          "description": "TEST TEST",
          "difficulty": "EASY"
        }
        """;

    mockMvc
        .perform(
            post("/api/expeditions")
                .with(user(userPrincipal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isCreated());
  }

  @Test
  @Disabled
  void adminDeletesExpedition() throws Exception {
    authService.register(new RegisterRequest(new SignupRequest("ABC", "DEF"), Role.ADMIN, null));

    User userPrincipal =
        userRepository
            .findByUsername("ABC")
            .orElseThrow(() -> new RuntimeException("User not found"));

    UUID id = UUID.randomUUID();
    ExpeditionEntity e =
        new ExpeditionEntity(
            id, "abc", "def", Difficulty.EASY, ExpeditionStatus.CREATED, null, null, null);

    expeditionRepository.saveAndFlush(e);

    mockMvc
        .perform(delete("/api/expeditions/" + id).with(user(userPrincipal)))
        .andExpect(status().isOk());
  }
}
