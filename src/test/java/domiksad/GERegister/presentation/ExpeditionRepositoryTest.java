package domiksad.GERegister.presentation;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ExpeditionRepositoryTest {

    @Autowired
    private ExpeditionRepository expeditionRepository;

    @Autowired
    private HunterRepository hunterRepository;

    @Test
    void shouldSaveAndFindExpedition() {
        ExpeditionEntity expedition = new ExpeditionEntity();
        expedition.setName("Everest");

        ExpeditionEntity saved = expeditionRepository.save(expedition);

        Optional<ExpeditionEntity> found = expeditionRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Everest", found.get().getName());
    }

    @Test
    void shouldSaveExpeditionWithHunters(){
        HunterEntity hunter = new HunterEntity();
        hunter.setName("Geralt");
        hunter = hunterRepository.save(hunter);

        ExpeditionEntity expedition = new ExpeditionEntity();
        expedition.setName("Monster Hunt");
        expedition.setHunterEntityList(List.of(hunter));

        ExpeditionEntity saved = expeditionRepository.save(expedition);

        ExpeditionEntity found = expeditionRepository.findById(saved.getId()).orElseThrow();

        assertEquals(1, found.getHunterEntityList().size());
        assertEquals("Geralt", found.getHunterEntityList().get(0).getName());
    }

    @Test
    void shouldLinkHunterToExpedition() {
        ExpeditionEntity expedition = new ExpeditionEntity();
        expedition.setName("Dragon Hunt");
        expedition = expeditionRepository.save(expedition);

        HunterEntity hunter = new HunterEntity();
        hunter.setName("Ciri");
        hunter.setExpeditions(List.of(expedition));

        hunter = hunterRepository.save(hunter);

        HunterEntity found = hunterRepository.findById(hunter.getId()).orElseThrow();

        assertEquals(1, found.getExpeditions().size());
        assertEquals("Dragon Hunt", found.getExpeditions().get(0).getName());
    }

    @Test
    void shouldDeleteExpedition() {
        ExpeditionEntity expedition = new ExpeditionEntity();
        expedition.setName("Test");
        ExpeditionEntity saved = expeditionRepository.save(expedition);

        expeditionRepository.deleteById(saved.getId());

        Optional<ExpeditionEntity> found = expeditionRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
