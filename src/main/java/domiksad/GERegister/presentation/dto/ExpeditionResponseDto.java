package domiksad.GERegister.presentation.dto;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import java.time.Instant;
import java.util.UUID;

public record ExpeditionResponseDto(
    UUID id,
    String name,
    String description,
    Difficulty difficulty,
    ExpeditionStatus status,
    Instant startDate,
    Instant finishDate) {}
