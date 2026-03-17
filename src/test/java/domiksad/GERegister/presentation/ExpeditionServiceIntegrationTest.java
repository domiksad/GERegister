package domiksad.GERegister.presentation;

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
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class ExpeditionServiceIntegrationTest {

    @Autowired
    private ExpeditionService expeditionService;

    @Autowired
    private HunterService hunterService;

    @Autowired
    private ExpeditionRepository expeditionRepository;

    @Autowired
    private HunterRepository hunterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldCreateExpeditionAndSaveToDatabase() {
        ExpeditionRequestDto dto = new ExpeditionRequestDto();
        dto.setName("Everest");
        dto.setDifficulty(Difficulty.HARD);

        ExpeditionResponseDto result = expeditionService.createExpedition(dto);

        assertNotNull(result.getId());

        ExpeditionEntity entity = expeditionRepository.findById(result.getId()).orElseThrow();
        assertEquals("Everest", entity.getName());
    }

    @Test
    void shouldAssignHunterToExpedition() {
        HunterRequestDto hunterDto = new HunterRequestDto();
        hunterDto.setName("Geralt");
        HunterResponseDto hunter = hunterService.createHunter(hunterDto);

        assertTrue(hunterRepository.findById(hunter.getId()).isPresent());

        ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto();
        expeditionDto.setName("Leshen Hunt");
        ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

        entityManager.flush();entityManager.clear();

        expeditionService.assignHunterToExpedition(expedition.getId(), hunter.getId());

        ExpeditionEntity saved = expeditionRepository.findById(expedition.getId()).orElseThrow();

        assertEquals(1, saved.getHunterEntityList().size());
    }

    @Test
    void shouldThrowWhenAssigningNonExistingHunter() {
        ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto();
        expeditionDto.setName("Test");
        ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

        assertThrows(HunterNotFoundException.class, () ->
                expeditionService.assignHunterToExpedition(expedition.getId(), 999L)
        );
    }

    @Test
    void shouldStartExpedition() {
        HunterResponseDto hunterDto = hunterService.createHunter(new HunterRequestDto("Gerald"));

        ExpeditionRequestDto dto = new ExpeditionRequestDto();
        dto.setName("Start test");

        entityManager.flush();entityManager.clear();

        ExpeditionResponseDto expedition = expeditionService.createExpedition(dto);

        entityManager.flush();entityManager.clear();

        expeditionService.assignHunterToExpedition(expedition.getId(), hunterDto.getId());

        entityManager.flush();entityManager.clear();

        ExpeditionResponseDto started = expeditionService.startExpedition(expedition.getId());

        assertEquals(ExpeditionStatus.IN_PROGRESS, started.getStatus());
    }

    @Test
    void shouldRemoveHunterFromExpedition() {
        HunterRequestDto hunterDto = new HunterRequestDto();
        hunterDto.setName("Ciri");
        HunterResponseDto hunter = hunterService.createHunter(hunterDto);

        ExpeditionRequestDto expeditionDto = new ExpeditionRequestDto();
        expeditionDto.setName("Test remove");
        ExpeditionResponseDto expedition = expeditionService.createExpedition(expeditionDto);

        entityManager.flush();entityManager.clear();

        expeditionService.assignHunterToExpedition(expedition.getId(), hunter.getId());
        expeditionService.removeHunterFromExpedition(expedition.getId(), hunter.getId());

        ExpeditionEntity saved = expeditionRepository.findById(expedition.getId()).orElseThrow();

        assertTrue(saved.getHunterEntityList().isEmpty());
    }
}