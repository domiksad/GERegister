package domiksad.GERegister.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.service.ExpeditionService;
import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class ExpeditionServiceIntegrationTest {
  // TODO: fix mappings and add tests for them, then fix this test

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
  void shouldThrowWhenAssigningNonExistingHunter() {
    ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto("Test", "Test", Difficulty.EASY);
    ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

    assertThrows(
        HunterNotFoundException.class,
        () -> expeditionService.assignHunterToExpedition(expedition.id(), UUID.randomUUID()));
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
}
