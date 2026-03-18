package domiksad.GERegister.presentation.dto;

import domiksad.GERegister.domain.expedition.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExpeditionRequestDto(
    @NotBlank(message = "Field 'name' is required and cannot be empty") String name,
    @NotBlank(message = "Field 'description' is required and cannot be empty") String description,
    @NotNull(message = "Field 'difficulty' is required") Difficulty difficulty) {}
