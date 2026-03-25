package domiksad.GERegister.presentation;

import domiksad.GERegister.application.service.ExpeditionService;
import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class HunterServiceIntegrationTest {

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
  void shouldCreateHunterAndSaveToDatabase(){
    HunterRequestDto dto = new HunterRequestDto("Test");

    HunterResponseDto result = hunterService.createHunter(dto);

    assertNotNull(result.id());

    HunterEntity entity = hunterRepository.findById(result.id()).orElseThrow();

    assertEquals("Test", entity.getName());
  }
}