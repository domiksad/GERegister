package domiksad.GERegister.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ExpeditionRepository expeditionRepository;
  @Autowired private HunterRepository hunterRepository;
  @Autowired private UserRepository userRepository;

  @Autowired private AuthService authService;

  @BeforeEach
  void clean() {
    userRepository.deleteAll();
    expeditionRepository.deleteAll();
    hunterRepository.deleteAll();
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

  private JwtRequestPostProcessor getJwtAdmin() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  private JwtRequestPostProcessor getJwtArchivist() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_ARCHIVIST"));
  }

  private JwtRequestPostProcessor getJwtCommander() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_COMMANDER"));
  }

  private JwtRequestPostProcessor getJwtHunter() {
    return jwt().authorities(new SimpleGrantedAuthority("ROLE_HUNTER"));
  }

  // <editor-fold desc="getAllExpeditions">
  private void getAllExpeditionsWithoutFilters(JwtRequestPostProcessor role) throws Exception {
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
        .perform(get("/api/expeditions").with(role).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.page.totalElements").value(2))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.number").value(0));
  }

  @Test
  void getAllExpeditionsWithoutFiltersWithAdmin() throws Exception {
    getAllExpeditionsWithoutFilters(getJwtAdmin());
  }

  @Test
  void getAllExpeditionsWithoutFiltersWithArchivist() throws Exception {
    getAllExpeditionsWithoutFilters(getJwtArchivist());
  }

  @Test
  void getAllExpeditionsWithoutFiltersWithCommander() throws Exception {
    getAllExpeditionsWithoutFilters(getJwtCommander());
  }

  @Test
  void getAllExpeditionsWithoutFiltersWithHunter_ThrowsException() throws Exception {
    mockMvc.perform(get("/api/expeditions").with(getJwtHunter())).andExpect(status().isForbidden());
  }

  @Test
  void getAllExpeditionsWithoutFiltersAnonymous_ThrowsException() throws Exception {
    mockMvc.perform(get("/api/expeditions")).andExpect(status().isUnauthorized());
  }

  @Test
  void getAllExpeditionsWithFilters() throws Exception {
    expeditionRepository.save(
        new ExpeditionEntity(
            null,
            "Test 1",
            "Test object 1 abc",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>()));
    expeditionRepository.save(
        new ExpeditionEntity(
            null,
            "Test 2",
            "Test object 2 abc",
            Difficulty.HARD,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>()));
    expeditionRepository.save(
        new ExpeditionEntity(
            null,
            "Test 3",
            "Test object 3 def",
            Difficulty.EASY,
            ExpeditionStatus.IN_PROGRESS,
            Instant.now(),
            null,
            new HashSet<>()));

    // Filter by name
    mockMvc
        .perform(
            get("/api/expeditions")
                .with(getJwtAdmin())
                .param("name", "Test 1")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Test 1"))
        .andExpect(jsonPath("$.page.totalElements").value(1));

    // Filter by difficulty
    mockMvc
        .perform(
            get("/api/expeditions")
                .with(getJwtAdmin())
                .param("difficulty", "EASY")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.page.totalElements").value(2));

    // Filter by status
    mockMvc
        .perform(
            get("/api/expeditions")
                .with(getJwtAdmin())
                .param("status", "IN_PROGRESS")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"))
        .andExpect(jsonPath("$.page.totalElements").value(1));

    // Filter by difficulty and status
    mockMvc
        .perform(
            get("/api/expeditions")
                .with(getJwtAdmin())
                .param("difficulty", "EASY")
                .param("status", "CREATED")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].name").value("Test 1"))
        .andExpect(jsonPath("$.page.totalElements").value(1));

    // Filter and find nothing
    mockMvc
        .perform(
            get("/api/expeditions")
                .with(getJwtAdmin())
                .param("name", "nonexistent")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.page.totalElements").value(0))
        .andExpect(jsonPath("$.page.number").value(0));
  }

  // </editor-fold>

  // <editor-fold desc="getExpeditionById">
  private void getExpeditionById(JwtRequestPostProcessor role) throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    mockMvc
        .perform(get("/api/expeditions/" + id).with(role).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Test"))
        .andExpect(jsonPath("$.difficulty").value("EASY"))
        .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getExpeditionByIdWithAdmin() throws Exception {
    getExpeditionById(getJwtAdmin());
  }

  @Test
  void getExpeditionByIdWithArchivist() throws Exception {
    getExpeditionById(getJwtArchivist());
  }

  @Test
  void getExpeditionByIdWithCommander() throws Exception {
    getExpeditionById(getJwtCommander());
  }

  @Test
  void getExpeditionByIdWithHunterInExpedition() throws Exception {
    HunterEntity hunter = hunterRepository.save(new HunterEntity(null, "Test"));
    User user = userRepository.save(new User(null, "User", "user", Set.of(Role.HUNTER), hunter));

    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>(Set.of(hunter))))
            .getId();

    mockMvc
        .perform(get("/api/expeditions/" + id).with(user(user)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.name").value("Test"))
        .andExpect(jsonPath("$.difficulty").value("EASY"))
        .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getExpeditionByIdWithHunterNotInExpedition_ThrowsException() throws Exception {
    HunterEntity hunter = hunterRepository.save(new HunterEntity(null, "Test"));
    User user = userRepository.save(new User(null, "User", "user", Set.of(Role.HUNTER), hunter));

    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    mockMvc
        .perform(get("/api/expeditions/" + id).with(user(user)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  void getExpeditionByIdWithAnonymous_ThrowsException() throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>(Set.of())))
            .getId();

    mockMvc
        .perform(get("/api/expeditions/" + id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getExpeditionByIdWithoutParameter_throwsException() throws Exception {
    mockMvc.perform(get("/api/expeditions/").with(getJwtAdmin())).andExpect(status().isNotFound());
  }

  // </editor-fold>

  // <editor-fold desc="getHuntersAssignedToExpedition">
  private void getHuntersAssignedToExpedition(JwtRequestPostProcessor role) throws Exception {
    HunterEntity h1 = hunterRepository.save(new HunterEntity(null, "Hunter 1"));
    HunterEntity h2 = hunterRepository.save(new HunterEntity(null, "Hunter 2"));
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>(Set.of(h1, h2))))
            .getId();

    mockMvc
        .perform(
            get("/api/expeditions/" + id + "/hunters")
                .with(role)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath("$[*].name")
                .value(org.hamcrest.Matchers.containsInAnyOrder("Hunter 1", "Hunter 2")));
  }

  @Test
  void getHuntersAssignedToExpeditionWithAdmin() throws Exception {
    getHuntersAssignedToExpedition(getJwtAdmin());
  }

  @Test
  void getHuntersAssignedToExpeditionWithArchivist() throws Exception {
    getHuntersAssignedToExpedition(getJwtArchivist());
  }

  @Test
  void getHuntersAssignedToExpeditionWithCommander() throws Exception {
    getHuntersAssignedToExpedition(getJwtCommander());
  }

  @Test
  void getHuntersAssignedToExpeditionWithHunterInExpedition() throws Exception {
    HunterEntity h1 = hunterRepository.save(new HunterEntity(null, "Hunter 1"));
    User user = userRepository.save(new User(null, "User", "user", Set.of(Role.HUNTER), h1));

    HunterEntity h2 = hunterRepository.save(new HunterEntity(null, "Hunter 2"));
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test object",
                    Difficulty.EASY,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>(Set.of(h1, h2))))
            .getId();

    mockMvc
        .perform(
            get("/api/expeditions/" + id + "/hunters")
                .with(user(user))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath("$[*].name")
                .value(org.hamcrest.Matchers.containsInAnyOrder("Hunter 1", "Hunter 2")));
  }

  @Test
  void getHunterAssignedExpeditionWithHunterNotInExpedition_throwsException() throws Exception {
    mockMvc
        .perform(get("/api/expeditions/" + UUID.randomUUID() + "/hunters").with(getJwtHunter()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getHunterAssignedExpeditionWithAnonymous_throwsException() throws Exception {
    mockMvc
        .perform(get("/api/expeditions/" + UUID.randomUUID()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getHunterAssignedExpeditionWithoutParameter_throwsException() throws Exception {
    mockMvc.perform(get("/api/expeditions/").with(getJwtAdmin())).andExpect(status().isNotFound());
  }

  // </editor-fold>

  // <editor-fold desc="createExpedition">
  private void createExpedition(JwtRequestPostProcessor role) throws Exception {
    String req =
        """
         {
          "name": "TEST",
          "description": "TEST DESCRIPTION",
          "difficulty": "HARD"
          }
         """;

    mockMvc
        .perform(
            post("/api/expeditions")
                .contentType(MediaType.APPLICATION_JSON)
                .with(role)
                .content(req)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isString())
        .andExpect(jsonPath("$.name").value("TEST"))
        .andExpect(jsonPath("$.description").value("TEST DESCRIPTION"))
        .andExpect(jsonPath("$.difficulty").value("HARD"))
        .andExpect(jsonPath("$.status").value("CREATED"))
        .andExpect(jsonPath("$.startDate").isEmpty())
        .andExpect(jsonPath("$.finishDate").isEmpty());
  }

  @Test
  void createExpeditionWithAdmin() throws Exception {
    createExpedition(getJwtAdmin());
  }

  @Test
  void createExpeditionWithCommander() throws Exception {
    createExpedition(getJwtCommander());
  }

  @Test
  void createExpeditionWithArchivist_throwsException() throws Exception {
    String req =
        """
         {
          "name": "TEST",
          "description": "TEST DESCRIPTION",
          "difficulty": "HARD"
          }
        """;

    mockMvc
        .perform(
            post("/api/expeditions")
                .contentType(MediaType.APPLICATION_JSON)
                .with(getJwtArchivist())
                .content(req))
        .andExpect(status().isForbidden());
  }

  @Test
  void createExpeditionWithHunter_throwsException() throws Exception {
    String req =
        """
                 {
                  "name": "TEST",
                  "description": "TEST DESCRIPTION",
                  "difficulty": "HARD"
                  }
                 """;

    mockMvc
        .perform(
            post("/api/expeditions")
                .contentType(MediaType.APPLICATION_JSON)
                .with(getJwtHunter())
                .content(req))
        .andExpect(status().isForbidden());
  }

  @Test
  void createExpeditionWithAnonymous_throwsException() throws Exception {
    String req =
        """
         {
          "name": "TEST",
          "description": "TEST DESCRIPTION",
          "difficulty": "HARD"
          }
         """;

    mockMvc
        .perform(post("/api/expeditions").contentType(MediaType.APPLICATION_JSON).content(req))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createExpeditionWithMissingParameters_throwsException() throws Exception {
    String req =
        """
        {
          "name": "abc"
        }
        """;
    mockMvc
        .perform(
            post("/api/expeditions")
                .with(getJwtAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createExpeditionWithoutParameters_throwsException() throws Exception {
    mockMvc
        .perform(post("/api/expeditions").with(getJwtAdmin()))
        .andExpect(status().isBadRequest());
  }

  // </editor-fold>

  // <editor-fold desc="updateExpedition">
  private void updateExpedition(JwtRequestPostProcessor role) throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test",
                    Difficulty.HARD,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + id.toString())
                .with(role)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("abc"))
        .andExpect(jsonPath("$.description").value("abcdef"))
        .andExpect(jsonPath("$.difficulty").value("EASY"));

    ExpeditionEntity updatedEntity = expeditionRepository.findById(id).orElseThrow();
    assertEquals("abc", updatedEntity.getName());
    assertEquals("abcdef", updatedEntity.getDescription());
    assertEquals(Difficulty.EASY, updatedEntity.getDifficulty());
    assertEquals(ExpeditionStatus.CREATED, updatedEntity.getStatus());
  }

  @Test
  void updateExpeditionWithAdmin() throws Exception {
    updateExpedition(getJwtAdmin());
  }

  @Test
  void updateExpeditionWithCommander() throws Exception {
    updateExpedition(getJwtAdmin());
  }

  @Test
  void updateExpeditionWithArchivist_throwsException() throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test",
                    Difficulty.HARD,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + id.toString())
                .with(getJwtArchivist())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateExpeditionWithHunter_throwsException() throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test",
                    Difficulty.HARD,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + id.toString())
                .with(getJwtHunter())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isForbidden());
  }

  @Test
  void updateExpeditionWithAnonymous_throwsException() throws Exception {
    UUID id =
        expeditionRepository
            .save(
                new ExpeditionEntity(
                    null,
                    "Test",
                    "Test",
                    Difficulty.HARD,
                    ExpeditionStatus.CREATED,
                    null,
                    null,
                    new HashSet<>()))
            .getId();

    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + id.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void updateExpeditionWithoutExpeditionId_throwsException() throws Exception {
    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/")
                .with(getJwtAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateExpeditionWithBadId_throwsException() throws Exception {
    String req =
        """
      {
        "name": "abc",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + UUID.randomUUID())
                .with(getJwtAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateExpeditionWithBadJson_throwsException() throws Exception {
    String req =
        """
      {
        "name": "",
        "description": "abcdef",
        "difficulty": "EASY"
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + UUID.randomUUID())
                .with(getJwtAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateExpeditionWithBadJsonParameters_throwsException() throws Exception {
    String req =
        """
      {
        "name": "",
        "description": "abcdef",
        "difficulty": "EASY"
        }
      """;

    mockMvc
        .perform(
            put("/api/expeditions/" + UUID.randomUUID())
                .with(getJwtAdmin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isBadRequest());
  }
  // </editor-fold>
}
