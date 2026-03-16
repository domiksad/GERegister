package domiksad.GERegister.presentation.application.mappers;

import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class HunterMapperTest {

    private final HunterMapper mapper = Mappers.getMapper(HunterMapper.class);

    @Test
    void shouldMapRequestDtoToEntity() {
        HunterRequestDto dto = new HunterRequestDto("Gon");

        HunterEntity entity = mapper.hunterRequestDtoToHunterEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo(dto.getName());
    }

    @Test
    void shouldMapEntityToResponseDto() {
        HunterEntity entity = new HunterEntity();
        entity.setId(1L);
        entity.setName("Gon");

        HunterResponseDto dto = mapper.hunterEntityToHunterResponseDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(entity.getName());
    }
}