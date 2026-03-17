package domiksad.GERegister.application.service;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HunterService {

    private final HunterMapper hunterMapper;
    private final ExpeditionMapper expeditionMapper;
    @Autowired
    HunterRepository hunterRepository;

    public List<HunterResponseDto> getAllHunters() {
        return hunterRepository.findAll().stream().map(hunterMapper::hunterEntityToHunterResponseDto).toList();
    }

    public HunterResponseDto getHunterById(Long id) {
        return hunterMapper.hunterEntityToHunterResponseDto(hunterRepository.findById(id).orElseThrow(() -> new HunterNotFoundException(id)));
    }

    public List<ExpeditionResponseDto> getHuntersExpeditions(Long id) {
        return hunterRepository
                .findById(id).orElseThrow(() -> new HunterNotFoundException(id))
                .getExpeditions()
                .stream().map(expeditionMapper::expeditionEntityToExpeditionResponseDto)
                .toList();
    }

    public HunterResponseDto createHunter(HunterRequestDto hunterRequestDto) {
        return hunterMapper.hunterEntityToHunterResponseDto(hunterRepository.save(hunterMapper.hunterRequestDtoToHunterEntity(hunterRequestDto)));
    }

    public HunterResponseDto update(Long id, HunterRequestDto dto) {
        HunterEntity entity = hunterRepository.findById(id)
                .orElseThrow(() -> new HunterNotFoundException(id));

        entity.setName(dto.getName());

        return hunterMapper.hunterEntityToHunterResponseDto(
                hunterRepository.save(entity)
        );
    }

    public void deleteHunterById(Long id) {
        hunterRepository.deleteById(id);
    }
}
