package domiksad.GERegister.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class HunterMapperTest {
  private final HunterMapper mapper = Mappers.getMapper(HunterMapper.class);

  // requestDto -> Entity -> Domain -> Entity -> responseDto
  @Test
  void requestDtoToEntity() {
    HunterRequestDto h1 = new HunterRequestDto("Test");
    HunterEntity h2 = mapper.fromDtoToEntity(h1);

    assertEquals(h1.name(), h2.getName());
  }

  @Test
  void entityToDomain() {
    HunterEntity h1 = new HunterEntity(UUID.randomUUID(), "Test");
    Hunter h2 = mapper.fromEntity(h1);

    assertEquals(h1.getId(), h2.getId());
    assertEquals(h1.getName(), h2.getName());
  }

  @Test
  void domainToEntity() {
    Hunter h1 = new Hunter(UUID.randomUUID(), "Test");
    HunterEntity h2 = mapper.toEntity(h1);

    assertEquals(h1.getId(), h2.getId());
    assertEquals(h1.getName(), h2.getName());
  }

  @Test
  void entityToResponseDto() {
    HunterEntity h1 = new HunterEntity(UUID.randomUUID(), "Test");
    HunterResponseDto h2 = mapper.toDto(h1);

    assertEquals(h1.getId(), h2.id());
    assertEquals(h1.getName(), h2.name());
  }
}
