package domiksad.GERegister.presentation.application.mappers;

import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ExpeditionMapperTest {
    private final ExpeditionMapper mapper = Mappers.getMapper(ExpeditionMapper.class);

    @Test
    void shouldMapRequestDtoToEntity() {
        ExpeditionRequestDto dto = new ExpeditionRequestDto("Expedition 1", "Explore forest", Difficulty.EASY, ExpeditionStatus.CREATED);
        var entity = mapper.expeditionRequestDtoToExpeditionEntity(dto);
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(dto.getName());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getDifficulty()).isEqualTo(dto.getDifficulty());
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
    }

    @Test
    void shouldMapEntityToResponseDto() {
        ExpeditionEntity entity = new ExpeditionEntity();
        entity.setId(1L);
        entity.setName("Expedition 1");
        entity.setDescription("Explore forest");
        entity.setDifficulty(Difficulty.MEDIUM);
        entity.setStatus(ExpeditionStatus.IN_PROGRESS);
        entity.setStartDate(Instant.parse("2026-03-16T08:00:00Z"));
        entity.setFinishDate(Instant.parse("2026-03-20T08:00:00Z"));
        ExpeditionResponseDto dto = mapper.expeditionEntityToExpeditionResponseDto(entity);
        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(entity.getName());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getDifficulty()).isEqualTo(entity.getDifficulty());
        assertThat(dto.getStatus()).isEqualTo(entity.getStatus());
        assertThat(dto.getStartDate()).isEqualTo(entity.getStartDate());
        assertThat(dto.getFinishDate()).isEqualTo(entity.getFinishDate());
    }
}