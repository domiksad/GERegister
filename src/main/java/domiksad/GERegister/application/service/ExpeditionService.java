package domiksad.GERegister.application.service;

import domiksad.GERegister.application.exceptions.ExpeditionNotFoundException;
import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class ExpeditionService {

  private final ExpeditionRepository expeditionRepository;
  private final HunterRepository hunterRepository;

  private final HunterMapper hunterMapper;
  private final ExpeditionMapper expeditionMapper;

  public Page<ExpeditionResponseDto> getAllExpeditionsFiltered(Pageable pageable, String name, Difficulty difficulty, ExpeditionStatus status) {
    Page<ExpeditionEntity> entities = expeditionRepository.findAll((root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (name != null && !name.isEmpty()) {
        predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
      }
      if (difficulty != null) {
        predicates.add(cb.equal(root.get("difficulty"), difficulty));
      }
      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    }, pageable);

    return entities.map(expeditionMapper::toDto);
  }

  public ExpeditionResponseDto getExpeditionById(UUID id) {
    return expeditionMapper.toDto(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
  }

  public List<HunterResponseDto> getHuntersAssignedToExpedition(UUID id) {
    return expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)).getHunters().stream().map(hunterMapper::hunterEntityToHunterResponseDto).toList();
  }

  public ExpeditionResponseDto createExpedition(ExpeditionRequestDto expeditionRequestDto) {
    return expeditionMapper.toDto(expeditionRepository.save(expeditionMapper.fromDtoToEntity(expeditionRequestDto)));
  }

  public ExpeditionResponseDto update(UUID id, ExpeditionRequestDto dto) {
    ExpeditionEntity entity = expeditionRepository.findById(id)
        .orElseThrow(() -> new ExpeditionNotFoundException(id));

    entity.setName(dto.name());
    entity.setDescription(dto.description());
    entity.setDifficulty(dto.difficulty());

    return expeditionMapper.toDto(
        expeditionRepository.save(entity)
    );
  }

  public void deleteExpeditionById(UUID id) {
    expeditionRepository.deleteById(id);
  }

  public ExpeditionResponseDto assignHunterToExpedition(UUID expeditionId, UUID hunterId) {
    ExpeditionEntity expedition = expeditionRepository.findById(expeditionId)
        .orElseThrow(() -> new ExpeditionNotFoundException(expeditionId));

    HunterEntity hunter = hunterRepository.findById(hunterId)
        .orElseThrow(() -> new HunterNotFoundException(hunterId));

    expedition.getHunters().add(hunter);

    expeditionRepository.save(expedition);

    return expeditionMapper.toDto(expedition);
  }

  public ExpeditionResponseDto removeHunterFromExpedition(UUID expeditionId, UUID hunterId) {
    ExpeditionEntity expedition = expeditionRepository.findById(expeditionId)
        .orElseThrow(() -> new ExpeditionNotFoundException(expeditionId));

    HunterEntity hunter = hunterRepository.findById(hunterId)
        .orElseThrow(() -> new HunterNotFoundException(hunterId));

    expedition.getHunters().remove(hunter);

    expeditionRepository.save(expedition);

    return expeditionMapper.toDto(expedition);
  }

  public ExpeditionResponseDto startExpedition(UUID id) {
    Expedition expedition = expeditionMapper.fromEntity(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
    expedition.start();

    ExpeditionEntity saved = expeditionMapper.toEntity(expedition);
    saved.setId(id);
    expeditionRepository.save(saved);

    return expeditionMapper.toDto(expedition);
  }

  public ExpeditionResponseDto finishExpedition(UUID id) {
    Expedition expedition = expeditionMapper.fromEntity(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
    expedition.finish();

    ExpeditionEntity saved = expeditionMapper.toEntity(expedition);
    saved.setId(id);
    expeditionRepository.save(saved);

    return expeditionMapper.toDto(expedition);
  }
}
