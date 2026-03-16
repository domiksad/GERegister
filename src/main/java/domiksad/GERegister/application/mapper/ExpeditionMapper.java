package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExpeditionMapper {

    Expedition expeditionRequestDtoToExpedition(ExpeditionRequestDto expeditionRequestDto);

    ExpeditionResponseDto expeditionToExpeditionResponseDto(Expedition expedition);

    Expedition expeditionEntityToExpedition(ExpeditionEntity expeditionEntity);

    ExpeditionEntity expeditionToExpeditionEntity(Expedition expedition);

    default ExpeditionResponseDto expeditionEntityToExpeditionResponseDto(ExpeditionEntity expeditionEntity) {
        Expedition expedition = expeditionEntityToExpedition(expeditionEntity);
        return expeditionToExpeditionResponseDto(expedition);
    }

    default ExpeditionEntity expeditionRequestDtoToExpeditionEntity(ExpeditionRequestDto expeditionRequestDto) {
        Expedition expedition = expeditionRequestDtoToExpedition(expeditionRequestDto);
        return expeditionToExpeditionEntity(expedition);
    }
}
