package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "default")
public interface HunterMapper {
    HunterEntity fromDtoToEntity(HunterRequestDto hunterRequestDto);

    Hunter fromEntity(HunterEntity hunterEntity);

    HunterEntity toEntity(Hunter hunter);

    HunterResponseDto toDto(HunterEntity hunterEntity);
}
