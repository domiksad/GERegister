package domiksad.GERegister.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HunterRequestDto {
    @NotBlank(message = "Field 'name' is required and cannot be empty")
    private String name;
}
