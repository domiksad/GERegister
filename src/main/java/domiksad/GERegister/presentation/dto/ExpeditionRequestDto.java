package domiksad.GERegister.presentation.dto;

import domiksad.GERegister.domain.expedition.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpeditionRequestDto {

    @NotBlank(message = "Field 'name' is required and cannot be empty")
    private String name;

    @NotBlank(message = "Field 'description' is required and cannot be empty")
    private String description;

    @NotNull(message = "Field 'difficulty' is required")
    private Difficulty difficulty;
}
