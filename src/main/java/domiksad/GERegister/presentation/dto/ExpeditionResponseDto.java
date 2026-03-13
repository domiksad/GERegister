package domiksad.GERegister.presentation.dto;

import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor()
public class ExpeditionResponseDto {
    private Long id;
    private String name;
    private String description;
    private Difficulty difficulty;
    private ExpeditionStatus status;
    private Instant startDate;
    private Instant finishDate;
}
