package domiksad.GERegister.presentation.controller;

import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/hunters")
@Tag(name = "Hunters", description = "Endpoints for Hunter operations")
public class HunterController {
    private final HunterService hunterService;

    //<editor-fold desc="Hunter CRUD">
    @Operation(summary = "Get all hunters")
    @GetMapping
    public ResponseEntity<List<HunterResponseDto>> getAllHunters() {
        return ResponseEntity.ok(hunterService.getAllHunters());
    }

    @Operation(summary = "Get hunter by id")
    @GetMapping("/{id}")
    public ResponseEntity<HunterResponseDto> getHunterById(@PathVariable Long id) {
        return ResponseEntity.ok(hunterService.getHunterById(id));
    }

    @Operation(summary = "Get hunter's expeditions")
    @GetMapping("/{id}/expeditions")
    public ResponseEntity<List<ExpeditionResponseDto>> getHuntersExpeditions(@PathVariable Long id) {
        return ResponseEntity.ok(hunterService.getHuntersExpeditions(id));
    }

    @Operation(summary = "Create new hunter")
    @PostMapping
    public ResponseEntity<HunterResponseDto> createHunter(@Valid @RequestBody HunterRequestDto hunterRequestDto) {
        HunterResponseDto createdHunter = hunterService.createHunter(hunterRequestDto);
        return ResponseEntity.created(URI.create("/api/hunters/" + createdHunter.getId())).body(createdHunter);
    }

    @Operation(summary = "Update existing hunter by id")
    @PutMapping("/{id}")
    public ResponseEntity<HunterResponseDto> updateHunter(@PathVariable Long id, @Valid @RequestBody HunterRequestDto hunterRequestDto) {
        return ResponseEntity.ok(hunterService.update(id, hunterRequestDto));
    }

    @Operation(summary = "Delete existing hunter by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHunterByID(@PathVariable Long id){
        hunterService.deleteHunterById(id);
        return ResponseEntity.noContent().build();
    }
    //</editor-fold>
}
