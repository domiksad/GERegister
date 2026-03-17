package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = HunterMapper.class)
public interface ExpeditionMapper {

    Expedition expeditionRequestDtoToExpedition(ExpeditionRequestDto expeditionRequestDto);

    ExpeditionResponseDto expeditionToExpeditionResponseDto(Expedition expedition);

    @Mapping(source = "hunterEntityList", target = "hunters")
    Expedition expeditionEntityToExpedition(ExpeditionEntity expeditionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "hunters", target = "hunterEntityList")
    ExpeditionEntity expeditionToExpeditionEntity(Expedition expedition);

    ExpeditionResponseDto expeditionEntityToExpeditionResponseDto(ExpeditionEntity expeditionEntity);

    ExpeditionEntity expeditionRequestDtoToExpeditionEntity(ExpeditionRequestDto expeditionRequestDto);

}
