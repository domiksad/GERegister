package domiksad.GERegister.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import domiksad.GERegister.api.interfaces.WithAdmin;
import domiksad.GERegister.api.interfaces.WithArchivist;
import domiksad.GERegister.api.interfaces.WithCommander;
import domiksad.GERegister.api.interfaces.WithGenericHunter;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.security.AuthService;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class apiTest {
  @Autowired MockMvc mockMvc;
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

  private User getHunterUser() {
    return userRepository.save(
        new User(
            null,
            "User",
            "user",
            Set.of(Role.HUNTER),
            hunterRepository.save(new HunterEntity(null, "Hunter for user"))));
  }

  // <editor-fold desc="getAllExpeditions">
  private void getAllExpeditionsWithoutFilters() throws Exception {
    expeditionRepository.save(
        new ExpeditionEntity(
            null,
            "Test1",
            "Test object no. 1",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>()));
    expeditionRepository.save(
        new ExpeditionEntity(
            null,
            "Test2",
            "Test object no. 2",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>()));

    mockMvc
        .perform(get("/api/expeditions").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.totalElements").value(2))
        .andExpect(jsonPath("$.size").value(10));
  }

  @Test
  @WithAdmin
  void getAllExpeditionsWithoutFiltersWithAdmin() throws Exception {
    getAllExpeditionsWithoutFilters();
  }

  @Test
  @WithArchivist
  void getAllExpeditionsWithoutFiltersWithArchivist() throws Exception {
    getAllExpeditionsWithoutFilters();
  }

  @Test
  @WithCommander
  void getAllExpeditionsWithoutFiltersWithCommander() throws Exception {
    getAllExpeditionsWithoutFilters();
  }

  @Test
  @WithGenericHunter
  void getAllExpeditionsWithoutFiltersWithHunter_ThrowsException() throws Exception {
    mockMvc.perform(get("/api/expeditions")).andExpect(status().isForbidden());
  }

  @Test
  void getAllExpeditionsWithoutFiltersAnonymous_ThrowsException() throws Exception {
    mockMvc.perform(get("/api/expeditions")).andExpect(status().isForbidden());
  }
  // </editor-fold>

}
