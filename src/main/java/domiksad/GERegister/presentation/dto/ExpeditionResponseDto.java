package domiksad.GERegister.presentation.dto;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import java.time.Instant;

public record ExpeditionResponseDto(
    Long id,
    String name,
    String description,
    Difficulty difficulty,
    ExpeditionStatus status,
    Instant startDate,
    Instant finishDate) {}
