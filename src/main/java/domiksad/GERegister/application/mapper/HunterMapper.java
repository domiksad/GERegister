package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "default")
public interface HunterMapper {

    Hunter hunterRequestDtoToHunter(HunterRequestDto hunterRequestDto);

    HunterResponseDto hunterToHunterResponseDto(Hunter hunter);

    Hunter hunterEntityToHunter(HunterEntity hunterEntity);

    HunterEntity hunterToHunterEntity(Hunter hunter);

    HunterResponseDto hunterEntityToHunterResponseDto(HunterEntity hunterEntity);

    HunterEntity hunterRequestDtoToHunterEntity(HunterRequestDto hunterRequestDto);
}
