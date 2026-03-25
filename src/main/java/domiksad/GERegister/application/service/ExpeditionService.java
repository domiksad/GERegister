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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class ExpeditionService {

  private final ExpeditionRepository expeditionRepository;
  private final HunterRepository hunterRepository;

  private final HunterMapper hunterMapper;
  private final ExpeditionMapper expeditionMapper;

  public Page<ExpeditionResponseDto> getAllExpeditionsFiltered(
      Pageable pageable, String name, Difficulty difficulty, ExpeditionStatus status) {
    Page<ExpeditionEntity> entities =
        expeditionRepository.findAll(
            (root, query, cb) -> {
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
            },
            pageable);

    return entities.map(expeditionMapper::toDto);
  }

  public ExpeditionResponseDto getExpeditionById(UUID id) {
    return expeditionMapper.toDto(
        expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
  }

  public List<HunterResponseDto> getHuntersAssignedToExpedition(UUID id) {
    return expeditionRepository
        .findById(id)
        .orElseThrow(() -> new ExpeditionNotFoundException(id))
        .getHunters()
        .stream()
        .map(hunterMapper::toDto)
        .toList();
  }

  public ExpeditionResponseDto createExpedition(ExpeditionRequestDto expeditionRequestDto) {
    return expeditionMapper.toDto(
        expeditionRepository.save(expeditionMapper.fromDtoToEntity(expeditionRequestDto)));
  }

  public ExpeditionResponseDto update(UUID id, ExpeditionRequestDto dto) {
    ExpeditionEntity entity =
        expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id));

    entity.setName(dto.name());
    entity.setDescription(dto.description());
    entity.setDifficulty(dto.difficulty());

    return expeditionMapper.toDto(expeditionRepository.save(entity));
  }

  public void deleteExpeditionById(UUID id) {
    expeditionRepository.deleteById(id);
  }

  public ExpeditionResponseDto assignHunterToExpedition(UUID expeditionId, UUID hunterId) {
    ExpeditionEntity entity =
        expeditionRepository
            .findById(expeditionId)
            .orElseThrow(() -> new ExpeditionNotFoundException(expeditionId));

    HunterEntity hunterEntity =
        hunterRepository
            .findById(hunterId)
            .orElseThrow(() -> new HunterNotFoundException(hunterId));

    Expedition domain = expeditionMapper.fromEntity(entity);
    domain.addHunter(hunterMapper.fromEntity(hunterEntity));

    entity.addHunter(hunterEntity);

    ExpeditionEntity saved = expeditionRepository.save(entity);

    return expeditionMapper.toDto(saved);
  }

  public ExpeditionResponseDto removeHunterFromExpedition(UUID expeditionId, UUID hunterId) {
    ExpeditionEntity entity =
        expeditionRepository
            .findById(expeditionId)
            .orElseThrow(() -> new ExpeditionNotFoundException(expeditionId));

    HunterEntity hunterEntity =
        hunterRepository
            .findById(hunterId)
            .orElseThrow(() -> new HunterNotFoundException(hunterId));

    Expedition domain = expeditionMapper.fromEntity(entity);
    domain.removeHunter(hunterMapper.fromEntity(hunterEntity));

    entity.removeHunter(hunterId);

    ExpeditionEntity saved = expeditionRepository.save(entity);

    return expeditionMapper.toDto(saved);
  }

  public ExpeditionResponseDto startExpedition(UUID id) {
    ExpeditionEntity entity =
        expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id));

    Expedition domain = expeditionMapper.fromEntity(entity);

    boolean isAnyHunterBusy =
        entity.getHunters().stream()
            .anyMatch(
                h ->
                    expeditionRepository.existsByHuntersIdAndStatus(
                        h.getId(), ExpeditionStatus.IN_PROGRESS));

    domain.start(isAnyHunterBusy);

    entity.setStatus(domain.getStatus());
    entity.setStartDate(domain.getStartDate());

    return expeditionMapper.toDto(entity);
  }

  public ExpeditionResponseDto finishExpedition(UUID id) {
    Expedition expedition =
        expeditionMapper.fromEntity(
            expeditionRepository
                .findById(id)
                .orElseThrow(() -> new ExpeditionNotFoundException(id)));
    expedition.finish();

    ExpeditionEntity saved = expeditionMapper.toEntity(expedition);
    saved.setId(id);
    expeditionRepository.save(saved);

    return expeditionMapper.toDto(expedition);
  }
}
