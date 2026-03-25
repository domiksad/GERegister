package domiksad.GERegister.application.service;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.ExpeditionRepository;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
public class HunterService {

  private final HunterRepository hunterRepository;
  private final ExpeditionRepository expeditionRepository;

  private final HunterMapper hunterMapper;
  private final ExpeditionMapper expeditionMapper;

  public List<HunterResponseDto> getAllHunters() {
    return hunterRepository.findAll().stream().map(hunterMapper::toDto).toList();
  }

  public HunterResponseDto getHunterById(UUID id) {
    return hunterMapper.toDto(
        hunterRepository.findById(id).orElseThrow(() -> new HunterNotFoundException(id)));
  }

  public List<ExpeditionResponseDto> getHuntersExpeditions(UUID id) {
    return expeditionRepository.findAllByHuntersId(id).stream()
        .map(expeditionMapper::toDto)
        .toList();
  }

  public HunterResponseDto createHunter(HunterRequestDto hunterRequestDto) {
    return hunterMapper.toDto(
        hunterRepository.save(hunterMapper.fromDtoToEntity(hunterRequestDto)));
  }

  public HunterResponseDto update(UUID id, HunterRequestDto dto) {
    HunterEntity entity =
        hunterRepository.findById(id).orElseThrow(() -> new HunterNotFoundException(id));

    entity.setName(dto.name());

    return hunterMapper.toDto(hunterRepository.save(entity));
  }

  public void deleteHunterById(UUID id) {
    hunterRepository.deleteById(id);
  }
}
