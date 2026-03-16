package domiksad.GERegister.application.service;

import domiksad.GERegister.application.exceptions.ExpeditionNotFoundException;
import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.ExpeditionEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpeditionService {
    private final HunterMapper hunterMapper;
    private final ExpeditionMapper expeditionMapper;

    @Autowired
    ExpeditionRepository expeditionRepository;
    @Autowired
    HunterRepository hunterRepository;

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

        return entities.map(expeditionMapper::expeditionEntityToExpeditionResponseDto);
    }

    public ExpeditionResponseDto getExpeditionById(Long id) {
        return expeditionMapper.expeditionEntityToExpeditionResponseDto(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
    }

    public List<HunterResponseDto> getHuntersAssignedToExpedition(Long id) {
        return expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)).getHunterEntityList().stream().map(hunterMapper::hunterEntityToHunterResponseDto).toList();
    }

    public ExpeditionResponseDto createExpedition(ExpeditionRequestDto expeditionRequestDto) {
        return expeditionMapper.expeditionEntityToExpeditionResponseDto(expeditionRepository.save(expeditionMapper.expeditionRequestDtoToExpeditionEntity(expeditionRequestDto)));
    }

    public ExpeditionResponseDto update(Long id, ExpeditionRequestDto expeditionRequestDto) {
        expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id));
        ExpeditionEntity newExpedition = expeditionMapper.expeditionRequestDtoToExpeditionEntity(expeditionRequestDto);
        newExpedition.setId(id);
        return expeditionMapper.expeditionEntityToExpeditionResponseDto(expeditionRepository.save(newExpedition));
    }

    public void deleteExpeditionById(Long id) {
        expeditionRepository.deleteById(id);
    }

    public ExpeditionResponseDto assignHunterToExpedition(Long expeditionId, Long hunterId) {
        Expedition expedition = expeditionMapper.expeditionEntityToExpedition(expeditionRepository.findById(expeditionId).orElseThrow(() -> new ExpeditionNotFoundException(expeditionId)));
        Hunter hunter = hunterMapper.hunterEntityToHunter(hunterRepository.findById(expeditionId).orElseThrow(() -> new HunterNotFoundException(hunterId)));

        expedition.addHunter(hunter);

        ExpeditionEntity saved = expeditionMapper.expeditionToExpeditionEntity(expedition);
        saved.setId(expeditionId);
        expeditionRepository.save(saved);

        return expeditionMapper.expeditionEntityToExpeditionResponseDto(saved);
    }

    public ExpeditionResponseDto removeHunterFromExpedition(Long expeditionId, Long hunterId) {
        Expedition expedition = expeditionMapper.expeditionEntityToExpedition(expeditionRepository.findById(expeditionId).orElseThrow(() -> new ExpeditionNotFoundException(expeditionId)));
        Hunter hunter = hunterMapper.hunterEntityToHunter(hunterRepository.findById(expeditionId).orElseThrow(() -> new HunterNotFoundException(hunterId)));

        expedition.removeHunter(hunter);

        ExpeditionEntity saved = expeditionMapper.expeditionToExpeditionEntity(expedition);
        saved.setId(expeditionId);
        expeditionRepository.save(saved);

        return expeditionMapper.expeditionEntityToExpeditionResponseDto(saved);
    }

    public ExpeditionResponseDto startExpedition(Long id) {
        Expedition expedition = expeditionMapper.expeditionEntityToExpedition(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
        expedition.start();

        ExpeditionEntity saved = expeditionMapper.expeditionToExpeditionEntity(expedition);
        saved.setId(id);
        expeditionRepository.save(saved);

        return expeditionMapper.expeditionToExpeditionResponseDto(expedition);
    }

    public ExpeditionResponseDto finishExpedition(Long id) {
        Expedition expedition = expeditionMapper.expeditionEntityToExpedition(expeditionRepository.findById(id).orElseThrow(() -> new ExpeditionNotFoundException(id)));
        expedition.finish();

        ExpeditionEntity saved = expeditionMapper.expeditionToExpeditionEntity(expedition);
        saved.setId(id);
        expeditionRepository.save(saved);

        return expeditionMapper.expeditionToExpeditionResponseDto(expedition);
    }
}
