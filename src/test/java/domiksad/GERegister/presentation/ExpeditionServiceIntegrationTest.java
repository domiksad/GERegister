package domiksad.GERegister.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.service.ExpeditionService;
import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.exceptions.ExpeditionException;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ExpeditionServiceIntegrationTest {

  @Autowired private ExpeditionService expeditionService;

  @Autowired private HunterService hunterService;

  @Autowired private ExpeditionRepository expeditionRepository;

  @Autowired private HunterRepository hunterRepository;

  @BeforeEach
  void clean() {
    expeditionRepository.deleteAll();
    hunterRepository.deleteAll();
  }

  @Test
  void shouldCreateExpeditionAndSaveToDatabase() {
    ExpeditionRequestDto dto = new ExpeditionRequestDto("ABC", "DEF", Difficulty.HARD);

    ExpeditionResponseDto result = expeditionService.createExpedition(dto);

    assertNotNull(result.id());

    ExpeditionEntity entity = expeditionRepository.findById(result.id()).orElseThrow();

    assertEquals("ABC", entity.getName());
    assertEquals("DEF", entity.getDescription());
    assertEquals(Difficulty.HARD, entity.getDifficulty());
  }

  @Test
  void shouldAssignHunterToExpedition() {
    HunterRequestDto hunterDto = new HunterRequestDto("Geralt");
    HunterResponseDto hunter = hunterService.createHunter(hunterDto);

    assertTrue(hunterRepository.findById(hunter.id()).isPresent());

    ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto("ABC", "DEF", Difficulty.HARD);
    ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

    expeditionService.assignHunterToExpedition(expedition.id(), hunter.id());

    ExpeditionEntity saved = expeditionRepository.findById(expedition.id()).orElseThrow();

    assertEquals(1, saved.getHunters().size());
  }

  @Test
  void shouldRemoveHunterFromExpedition() {
    HunterRequestDto hunterDto = new HunterRequestDto("Ciri");
    HunterResponseDto hunter = hunterService.createHunter(hunterDto);

    ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto("Test", "Test", Difficulty.EASY);
    ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

    expeditionService.assignHunterToExpedition(expedition.id(), hunter.id());
    expeditionService.removeHunterFromExpedition(expedition.id(), hunter.id());

    ExpeditionEntity saved = expeditionRepository.findById(expedition.id()).orElseThrow();

    assertTrue(saved.getHunters().isEmpty());
  }

  @Test
  void shouldThrowWhenAssigningNonExistingHunter() {
    ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto("Test", "Test", Difficulty.EASY);
    ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

    assertThrows(
        HunterNotFoundException.class,
        () -> expeditionService.assignHunterToExpedition(expedition.id(), UUID.randomUUID()));
  }

  @Test
  void assignHunterToInProgressExpeditionThrowsException() {
    ExpeditionEntity expeditionEntity =
        new ExpeditionEntity(
            null,
            "Test",
            "Test",
            Difficulty.EASY,
            ExpeditionStatus.IN_PROGRESS,
            Instant.now(),
            null,
            new HashSet<>());

    UUID expeditionId = expeditionRepository.save(expeditionEntity).getId();
    UUID hunterId = hunterRepository.save(new HunterEntity(null, "Test")).getId();

    assertThrows(
        ExpeditionException.class,
        () -> expeditionService.assignHunterToExpedition(expeditionId, hunterId));
  }

  @Test
  void removeHunterFromFinishedExpeditionThrowsException(){
    HunterEntity hunter = hunterRepository.save(new HunterEntity(null, "Test"));
    ExpeditionEntity expeditionEntity =
        new ExpeditionEntity(
            null,
            "Test",
            "Test",
            Difficulty.EASY,
            ExpeditionStatus.FINISHED,
            Instant.now(),
            null,
            new HashSet<>(Set.of(hunter)));

    UUID expeditionId = expeditionRepository.save(expeditionEntity).getId();

    assertThrows(
        ExpeditionException.class,
        () -> expeditionService.removeHunterFromExpedition(expeditionId, hunter.getId()));
  }

  @Test
  void shouldStartExpedition() {
    HunterResponseDto hunterDto = hunterService.createHunter(new HunterRequestDto("Gerald"));

    ExpeditionRequestDto dto = new ExpeditionRequestDto("Test", "Test", Difficulty.EASY);

    ExpeditionResponseDto expedition = expeditionService.createExpedition(dto);

    expeditionService.assignHunterToExpedition(expedition.id(), hunterDto.id());

    ExpeditionResponseDto started = expeditionService.startExpedition(expedition.id());

    assertEquals(ExpeditionStatus.IN_PROGRESS, started.status());
  }
}
