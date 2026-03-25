package domiksad.GERegister.testContainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.security.entity.Role;
import domiksad.GERegister.security.entity.User;
import domiksad.GERegister.security.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Transactional
public class testContainerTest {
  @Container @ServiceConnection
  private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

  @Autowired private ExpeditionRepository expeditionRepository;
  @Autowired private HunterRepository hunterRepository;
  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  private void flush() {
    entityManager.flush();
    entityManager.clear();
  }

  @Test
  void writeAndReadExpeditionEntity() {
    ExpeditionEntity e =
        new ExpeditionEntity(
            null,
            "Test",
            "Test object",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>());

    UUID id = expeditionRepository.save(e).getId();
    flush();

    Optional<ExpeditionEntity> found = expeditionRepository.findById(id);

    assertTrue(found.isPresent());
    ExpeditionEntity res = found.get();

    assertEquals(id, res.getId());
    assertEquals(e.getName(), res.getName());
    assertEquals(e.getDescription(), res.getDescription());
    assertEquals(e.getDifficulty(), res.getDifficulty());
    assertEquals(e.getStatus(), res.getStatus());
  }

  @Test
  void writeAndReadHunterEntity() {
    HunterEntity h = new HunterEntity(null, "Test");

    UUID id = hunterRepository.save(h).getId();
    flush();

    Optional<HunterEntity> found = hunterRepository.findById(id);

    assertTrue(found.isPresent());
    HunterEntity res = found.get();

    assertEquals(id, res.getId());
    assertEquals(h.getName(), res.getName());
  }

  @Test
  void writeAndReadExpeditionEntityWithHunterRelation() {
    HunterEntity h = new HunterEntity(null, "Test");
    HunterEntity saved = hunterRepository.save(h);

    ExpeditionEntity e =
        new ExpeditionEntity(
            null,
            "Test",
            "Test object",
            Difficulty.EASY,
            ExpeditionStatus.FINISHED,
            Instant.now(),
            Instant.now().plusSeconds(3600),
            new HashSet<>(Set.of(saved)));

    UUID id = expeditionRepository.save(e).getId();
    flush();

    Optional<ExpeditionEntity> found = expeditionRepository.findById(id);

    assertTrue(found.isPresent());
    ExpeditionEntity res = found.get();

    assertEquals(id, res.getId());
    assertEquals(e.getName(), res.getName());
    assertEquals(e.getDescription(), res.getDescription());
    assertEquals(e.getDifficulty(), res.getDifficulty());
    assertEquals(e.getStatus(), res.getStatus());
    assertThat(res.getStartDate()).isCloseTo(e.getStartDate(), within(1, ChronoUnit.MILLIS));
    assertThat(res.getFinishDate()).isCloseTo(e.getFinishDate(), within(1, ChronoUnit.MILLIS));

    assertEquals(1, res.getHunters().size());
    assertTrue(res.getHunters().contains(h));
  }

  @Test
  void writeAndReadUserWithHunterRelation() {
    HunterEntity h = new HunterEntity(null, "Test");
    HunterEntity saved = hunterRepository.save(h);

    User user = new User(null, "Test", "Test", Set.of(Role.HUNTER), h);

    Long id = userRepository.save(user).getId();
    flush();

    Optional<User> found = userRepository.findById(id);

    assertTrue(found.isPresent());
    User res = found.get();

    assertEquals(id, res.getId());
    assertEquals(user.getUsername(), res.getUsername());
    assertEquals(user.getPassword(), res.getPassword());
    assertEquals(user.getRoles(), res.getRoles());
    assertEquals(user.getHunter(), res.getHunter());
  }

  @Test
  void writeSameUsernameTwice_throwsException() {
    User user1 = new User(null, "Test", "test", Set.of(Role.ADMIN), null);
    User user2 = new User(null, "Test", "testAbc", Set.of(Role.ADMIN), null);

    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          userRepository.save(user1);
          userRepository.save(user2);
          flush();
        });
  }

  @Test
  void shouldFilterByStatusAndDifficulty() {
    ExpeditionEntity easyCreated =
        new ExpeditionEntity(
            null,
            "Easy One",
            "Desc",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>());
    ExpeditionEntity hardStarted =
        new ExpeditionEntity(
            null,
            "Hard One",
            "Desc",
            Difficulty.HARD,
            ExpeditionStatus.IN_PROGRESS,
            null,
            null,
            new HashSet<>());

    expeditionRepository.save(easyCreated);
    expeditionRepository.save(hardStarted);
    flush();

    Page<ExpeditionEntity> easyResults =
        expeditionRepository.findAll(
            (root, query, cb) -> {
              return cb.equal(root.get("difficulty"), Difficulty.EASY);
            },
            PageRequest.of(0, 10));

    assertEquals(1, easyResults.getTotalElements());
    assertEquals("Easy One", easyResults.getContent().get(0).getName());
  }

  @Test
  void shouldFilterByNameCaseInsensitive() {
    ExpeditionEntity e =
        new ExpeditionEntity(
            null,
            "Dragon Quest",
            "Desc",
            Difficulty.HARD,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>());
    expeditionRepository.save(e);
    flush();

    String searchName = "dragon";
    Page<ExpeditionEntity> results =
        expeditionRepository.findAll(
            (root, query, cb) -> {
              return cb.like(cb.lower(root.get("name")), "%" + searchName.toLowerCase() + "%");
            },
            PageRequest.of(0, 10));

    assertEquals(1, results.getTotalElements());
    assertTrue(results.getContent().get(0).getName().contains("Dragon"));
  }

  @Test
  void shouldReturnEmptyPageWhenNoMatches() {
    ExpeditionEntity e =
        new ExpeditionEntity(
            null,
            "Test",
            "Desc",
            Difficulty.EASY,
            ExpeditionStatus.CREATED,
            null,
            null,
            new HashSet<>());
    expeditionRepository.save(e);
    flush();

    Page<ExpeditionEntity> results =
        expeditionRepository.findAll(
            (root, query, cb) -> {
              return cb.equal(root.get("difficulty"), Difficulty.HARD);
            },
            PageRequest.of(0, 10));

    assertEquals(0, results.getTotalElements());
  }

  @Test
  void allTablesClear() {
    assertEquals(0, expeditionRepository.count());
    assertEquals(0, hunterRepository.count());
    assertEquals(0, userRepository.count());
  }
}
