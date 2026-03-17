package domiksad.GERegister.presentation;

import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class HunterRepositoryTest {

    @Autowired
    private HunterRepository hunterRepository;

    @Test
    void shouldSaveAndFindHunter() {
        HunterEntity hunter = new HunterEntity();
        hunter.setName("Geralt");

        HunterEntity saved = hunterRepository.save(hunter);

        Optional<HunterEntity> found = hunterRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Geralt", found.get().getName());
    }

    @Test
    void shouldFindAllHunters() {
        // given
        hunterRepository.save(new HunterEntity(null, "Geralt", null));
        hunterRepository.save(new HunterEntity(null, "Ciri", null));

        // when
        List<HunterEntity> hunters = hunterRepository.findAll();

        // then
        assertEquals(2, hunters.size());
    }
}