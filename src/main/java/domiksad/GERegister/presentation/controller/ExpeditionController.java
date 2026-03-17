package domiksad.GERegister.presentation.controller;

import domiksad.GERegister.application.service.ExpeditionService;
import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/expeditions")
@Tag(name = "Expeditions", description = "Endpoints for Expeditions operations")
public class ExpeditionController {
    private final HunterService hunterService;
    private final ExpeditionService expeditionService;

    //<editor-fold desc="Expedition CRUD">
    @Operation(summary = "Get all expeditions with pagination and filtering")
    @GetMapping
    public ResponseEntity<Page<ExpeditionResponseDto>> getAllExpeditions(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) ExpeditionStatus status
    ) {
        Page<ExpeditionResponseDto> page = expeditionService.getAllExpeditionsFiltered(pageable, name, difficulty, status);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get expedition by id")
    @GetMapping("/{id}")
    public ResponseEntity<ExpeditionResponseDto> getExpeditionById(@PathVariable Long id) {
        return ResponseEntity.ok(expeditionService.getExpeditionById(id));
    }

    @Operation(summary = "Get hunters assigned to expedition")
    @GetMapping("/{id}/hunters")
    public ResponseEntity<List<HunterResponseDto>> getHuntersAssignedToExpedition(@PathVariable Long id) {
        return ResponseEntity.ok(expeditionService.getHuntersAssignedToExpedition(id));
    }

    @Operation(summary = "Create new expedition")
    @PostMapping
    public ResponseEntity<ExpeditionResponseDto> createExpedition(@Valid @RequestBody ExpeditionRequestDto expeditionRequestDto) {
        ExpeditionResponseDto createdExpedition = expeditionService.createExpedition(expeditionRequestDto);
        return ResponseEntity.created(URI.create("/api/expeditions/" + createdExpedition.getId())).body(createdExpedition);
    }

    @Operation(summary = "Update existing expedition by id")
    @PutMapping("/{id}")
    public ResponseEntity<ExpeditionResponseDto> updateExpedition(@PathVariable Long id, @Valid @RequestBody ExpeditionRequestDto expeditionRequestDto) {
        return ResponseEntity.ok(expeditionService.update(id, expeditionRequestDto));
    }

    @Operation(summary = "Delete existing expedition by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpeditionById(@PathVariable Long id) {
        expeditionService.deleteExpeditionById(id);
        return ResponseEntity.noContent().build();
    }
    //</editor-fold>

    @Operation(summary = "Add hunter to expedition")
    @PutMapping("/{expeditionId}/{hunterId}")
    public ResponseEntity<ExpeditionResponseDto> assignHunterToExpedition(@PathVariable Long expeditionId, @PathVariable Long hunterId) {
        return ResponseEntity.ok(expeditionService.assignHunterToExpedition(expeditionId, hunterId));
    }

    @Operation(summary = "Remove hunter from expedition")
    @DeleteMapping("/{expeditionId}/{hunterId}")
    public ResponseEntity<ExpeditionResponseDto> removeHunterFomExpedition(@PathVariable Long expeditionId, @PathVariable Long hunterId) {
        return ResponseEntity.ok(expeditionService.removeHunterFromExpedition(expeditionId, hunterId));
    }

    @Operation(summary = "Start expedition")
    @PatchMapping("/{id}/start")
    public ResponseEntity<ExpeditionResponseDto> startExpedition(@PathVariable Long id) {
        return ResponseEntity.ok(expeditionService.startExpedition(id));
    }

    @Operation(summary = "End expedition")
    @PatchMapping("/{id}/end")
    public ResponseEntity<ExpeditionResponseDto> endExpedition(@PathVariable Long id) {
        return ResponseEntity.ok(expeditionService.finishExpedition(id));
    }
}
