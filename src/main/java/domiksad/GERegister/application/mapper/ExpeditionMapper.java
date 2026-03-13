package domiksad.GERegister.application.mapper;

import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExpeditionMapper {

    ExpeditionMapper INSTANCE = Mappers.getMapper(ExpeditionMapper.class);

    Expedition expeditionRequestDtoToExpedition(ExpeditionRequestDto expeditionRequestDto);

    ExpeditionResponseDto expeditionToExpeditionResponseDto(Expedition expedition);

    Expedition expeditionEntityToExpedition(ExpeditionEntity expeditionEntity);

    ExpeditionEntity expeditionToExpeditionEntity(Expedition expedition);

    default ExpeditionResponseDto expeditionEntityToExpeditionResponseDto(ExpeditionEntity expeditionEntity) {
        Expedition expedition = expeditionEntityToExpedition(expeditionEntity);
        return expeditionToExpeditionResponseDto(expedition);
    }
}
