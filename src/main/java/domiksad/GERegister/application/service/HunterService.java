package domiksad.GERegister.application.service;

import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import domiksad.GERegister.application.mapper.ExpeditionMapper;
import domiksad.GERegister.application.mapper.HunterMapper;
import domiksad.GERegister.domain.hunter.Hunter;
import domiksad.GERegister.infrastructure.entity.HunterEntity;
import domiksad.GERegister.infrastructure.repository.HunterRepository;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HunterService {

    @Autowired
    HunterRepository hunterRepository;

    public List<HunterResponseDto> getAllHunters() {
        return hunterRepository.findAll().stream().map(HunterMapper.INSTANCE::hunterEntityToHunterResponseDto).toList();
    }

    public HunterResponseDto getHunterById(Long id) {
        return HunterMapper.INSTANCE.hunterEntityToHunterResponseDto(hunterRepository.findById(id).orElseThrow(() -> new HunterNotFoundException(id)));
    }

    public List<ExpeditionResponseDto> getHuntersExpeditions(Long id) {
        return hunterRepository
                .findById(id).orElseThrow(() -> new HunterNotFoundException(id))
                .getExpeditions()
                .stream().map(ExpeditionMapper.INSTANCE::expeditionEntityToExpeditionResponseDto)
                .toList();
    }

    public HunterResponseDto createHunter(HunterRequestDto hunterRequestDto) {
        return HunterMapper.INSTANCE.hunterEntityToHunterResponseDto(hunterRepository.save(HunterMapper.INSTANCE.hunterRequestDtoToHunterEntity(hunterRequestDto)));
    }

    public HunterResponseDto update(Long id, HunterRequestDto hunterRequestDto) {
        hunterRepository.findById(id).orElseThrow(() -> new HunterNotFoundException(id));
        HunterEntity newHunter = HunterMapper.INSTANCE.hunterRequestDtoToHunterEntity(hunterRequestDto);
        newHunter.setId(id);
        return HunterMapper.INSTANCE.hunterEntityToHunterResponseDto(hunterRepository.save(newHunter));
    }

    public void deleteHunterById(Long id) {
        hunterRepository.deleteById(id);
    }
}
