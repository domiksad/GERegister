package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HunterMapper {

    HunterMapper INSTANCE = Mappers.getMapper(HunterMapper.class);

    Hunter hunterRequestDtoToHunter(HunterRequestDto hunterRequestDto);

    HunterResponseDto hunterToHunterResponseDto(Hunter hunter);

    Hunter hunterEntityToHunter(HunterEntity hunterEntity);

    HunterEntity hunterToHunterEntity(Hunter hunter);

    default HunterResponseDto hunterEntityToHunterResponseDto(HunterEntity hunterEntity) {
        Hunter hunter = hunterEntityToHunter(hunterEntity);
        return hunterToHunterResponseDto(hunter);
    }

    default HunterEntity hunterRequestDtoToHunterEntity(HunterRequestDto hunterRequestDto) {
        Hunter hunter = hunterRequestDtoToHunter(hunterRequestDto);
        return hunterToHunterEntity(hunter);
    }
}
