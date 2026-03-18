package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "default", uses = HunterMapper.class)
public interface ExpeditionMapper {
  ExpeditionEntity fromDtoToEntity(ExpeditionRequestDto expeditionRequestDto);

  Expedition fromEntity(ExpeditionEntity expeditionEntity);

  ExpeditionEntity toEntity(Expedition expedition);

  ExpeditionResponseDto toDto(ExpeditionEntity expeditionEntity);

  ExpeditionResponseDto toDto(Expedition expedition);
}
