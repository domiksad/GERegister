package domiksad.GERegister.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class ExpeditionMapperTest {
  private ExpeditionMapper mapper = Mappers.getMapper(ExpeditionMapper.class);

  private HashSet<HunterEntity> getHunterEntitySet() {
    HunterEntity h1 = new HunterEntity(UUID.randomUUID(), "Alice", new HashSet<>());
    HunterEntity h2 = new HunterEntity(UUID.randomUUID(), "Bob", new HashSet<>());
    HashSet<HunterEntity> hunters = new HashSet<>();
    hunters.add(h1);
    hunters.add(h2);
    return hunters;
  }

  private HashSet<Hunter> getHunterDomainSet() {
    Hunter h1 = new Hunter(UUID.randomUUID(), "Alice", new HashSet<>());
    Hunter h2 = new Hunter(UUID.randomUUID(), "Bob", new HashSet<>());
    HashSet<Hunter> hunters = new HashSet<>();
    hunters.add(h1);
    hunters.add(h2);
    return hunters;
  }

  // requestDto -> Entity -> Domain -> Entity -> responseDto
  @Test
  void requestDtoToEntity() {
    ExpeditionRequestDto e1 =
        new ExpeditionRequestDto("Test expedition", "Testing expedition mapping", Difficulty.EASY);
    ExpeditionEntity e2 = mapper.fromDtoToEntity(e1);

    assertEquals(e1.name(), e2.getName());
    assertEquals(e1.description(), e2.getDescription());
    assertEquals(e1.difficulty(), e2.getDifficulty());
  }

  @Test
  void entityToDomain() {
    ExpeditionEntity e1 =
        new ExpeditionEntity(
            UUID.randomUUID(),
            "Test expedition",
            "Testing expedition mapping",
            Difficulty.EASY,
            ExpeditionStatus.FINISHED,
            Instant.now(),
            Instant.now().plus(10, ChronoUnit.DAYS),
            getHunterEntitySet());
    Expedition e2 = mapper.fromEntity(e1);

    assertEquals(e1.getId(), e2.getId());
    assertEquals(e1.getName(), e2.getName());
    assertEquals(e1.getDescription(), e2.getDescription());
    assertEquals(e1.getDifficulty(), e2.getDifficulty());
    assertEquals(e1.getStatus(), e2.getStatus());
    assertEquals(e1.getStartDate(), e2.getStartDate());
    assertEquals(e1.getFinishDate(), e2.getFinishDate());
    e1.getHunters()
        .forEach(
            he ->
                assertTrue(
                    e2.getHunters().stream()
                        .anyMatch(
                            h ->
                                h.getId().equals(he.getId()) && h.getName().equals(he.getName()))));
  }

  @Test
  void domainToEntity() {
    Expedition e1 =
        new Expedition(
            UUID.randomUUID(),
            "Test expedition",
            "Testing expedition mapping",
            Difficulty.EASY,
            ExpeditionStatus.FINISHED,
            Instant.now(),
            Instant.now().plus(10, ChronoUnit.DAYS),
            getHunterDomainSet());
    ExpeditionEntity e2 = mapper.toEntity(e1);

    assertEquals(e1.getId(), e2.getId());
    assertEquals(e1.getName(), e2.getName());
    assertEquals(e1.getDescription(), e2.getDescription());
    assertEquals(e1.getDifficulty(), e2.getDifficulty());
    assertEquals(e1.getStatus(), e2.getStatus());
    assertEquals(e1.getStartDate(), e2.getStartDate());
    assertEquals(e1.getFinishDate(), e2.getFinishDate());
    e1.getHunters()
        .forEach(
            h ->
                assertTrue(
                    e2.getHunters().stream()
                        .anyMatch(
                            he ->
                                he.getId().equals(h.getId()) && he.getName().equals(h.getName()))));
  }

  @Test
  void entityToResponseDto() {
    ExpeditionEntity e1 =
        new ExpeditionEntity(
            UUID.randomUUID(),
            "Test expedition",
            "Testing expedition mapping",
            Difficulty.EASY,
            ExpeditionStatus.FINISHED,
            Instant.now(),
            Instant.now().plus(10, ChronoUnit.DAYS),
            getHunterEntitySet());
    ExpeditionResponseDto e2 = mapper.toDto(e1);

    assertEquals(e1.getId(), e2.id());
    assertEquals(e1.getName(), e2.name());
    assertEquals(e1.getDescription(), e2.description());
    assertEquals(e1.getDifficulty(), e2.difficulty());
    assertEquals(e1.getStatus(), e2.status());
    assertEquals(e1.getStartDate(), e2.startDate());
    assertEquals(e1.getFinishDate(), e2.finishDate());
  }
}
