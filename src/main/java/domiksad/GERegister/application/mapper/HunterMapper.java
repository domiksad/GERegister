package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default")
public interface HunterMapper {
  @Mapping(target="id", ignore = true)
  HunterEntity fromDtoToEntity(HunterRequestDto hunterRequestDto);

  Hunter fromEntity(HunterEntity hunterEntity);

  HunterEntity toEntity(Hunter hunter);

  HunterResponseDto toDto(HunterEntity hunterEntity);
}
