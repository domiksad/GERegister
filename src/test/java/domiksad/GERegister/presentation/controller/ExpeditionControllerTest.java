package domiksad.GERegister.presentation.controller;

import domiksad.GERegister.application.service.ExpeditionService;
import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.presentation.dto.ExpeditionRequestDto;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ExpeditionController.class)
class ExpeditionControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ExpeditionService expeditionService;
    @MockitoBean
    HunterService hunterService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

//    @Test
//    void getAllExpeditions() {
//        List<ExpeditionResponseDto> expeditions = List.of(
//                new ExpeditionResponseDto(1L, "Exp1", "Desc1", Difficulty.EASY, ExpeditionStatus.CREATED, null, null),
//                new ExpeditionResponseDto(2L, "Exp2", "Desc2", Difficulty.MEDIUM, ExpeditionStatus.CREATED, null, null));
//
//        Page<ExpeditionResponseDto> page = new PageImpl<>(expeditions, PageRequest.of(0, 10), expeditions.size());
//
//        when(expeditionService.getAllExpeditionsFiltered(any()))
//                .thenReturn(page);
//
//        given()
//                .when()
//                .get("/api/expeditions")
//                .then()
//                .statusCode(200)
//                .body("content[0].name", equalTo("Exp1"))
//                .body("content[1].difficulty", equalTo("MEDIUM"))
//                .body("totalElements", equalTo(2));
//    }

    @Test
    void getExpeditionById() {
        ExpeditionResponseDto expedition = new ExpeditionResponseDto(1L, "Exp1", "Desc1", Difficulty.EASY, ExpeditionStatus.CREATED, null, null);

        when(expeditionService.getExpeditionById(1L))
                .thenReturn(expedition);

        given()
                .when()
                .get("/api/expeditions/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Exp1"))
                .body("difficulty", equalTo("EASY"));
    }

    @Test
    void getHuntersAssignedToExpedition() {
        List<HunterResponseDto> hunters = List.of(new HunterResponseDto(1L, "Gon"), new HunterResponseDto(2L, "Killua"));

        when(expeditionService.getHuntersAssignedToExpedition(1L))
                .thenReturn(hunters);

        given()
                .when()
                .get("/api/expeditions/1/hunters")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Gon"))
                .body("[1].id", equalTo(2));
    }

    @Test
    void createExpedition() {
        ExpeditionRequestDto request = new ExpeditionRequestDto("Exp1", "Desc1", Difficulty.EASY, ExpeditionStatus.CREATED);
        ExpeditionResponseDto response = new ExpeditionResponseDto(1L, "Exp1", "Desc1", Difficulty.EASY, ExpeditionStatus.CREATED, null, null);

        when(expeditionService.createExpedition(any(ExpeditionRequestDto.class)))
                .thenReturn(response);

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/expeditions")
                .then()
                .statusCode(201)
                .body("name", equalTo("Exp1"));
    }

    @Test
    void updateExpedition() {
        ExpeditionRequestDto request = new ExpeditionRequestDto("ExpUpdated", "DescUpdated", Difficulty.HARD, ExpeditionStatus.IN_PROGRESS);
        ExpeditionResponseDto response = new ExpeditionResponseDto(1L, "ExpUpdated", "DescUpdated", Difficulty.HARD, ExpeditionStatus.IN_PROGRESS, null, null);

        when(expeditionService.update(any(Long.class), any(ExpeditionRequestDto.class)))
                .thenReturn(response);

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .put("/api/expeditions/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("ExpUpdated"))
                .body("status", equalTo("IN_PROGRESS"));
    }

    @Test
    void deleteExpeditionById() {
        given()
                .when().
                delete("/api/expeditions/1")
                .then()
                .statusCode(204);

        verify(expeditionService).deleteExpeditionById(1L);
    }

    @Test
    void assignHunterToExpedition_shouldReturnOk() {
        when(expeditionService.assignHunterToExpedition(anyLong(), anyLong()))
                .thenReturn(new ExpeditionResponseDto(1L, "Expedition 1", "Desc", Difficulty.EASY, ExpeditionStatus.CREATED, null, null));

        given()
                .when()
                .put("/api/expeditions/1/2")
                .then()
                .statusCode(200)
                .body("name", equalTo("Expedition 1"));
    }

    @Test
    void removeHunterFromExpedition_shouldReturnOk() {
        when(expeditionService.removeHunterFromExpedition(anyLong(), anyLong()))
                .thenReturn(new ExpeditionResponseDto(1L, "Expedition 1", "Desc", Difficulty.EASY, ExpeditionStatus.CREATED, null, null));

        given()
                .when()
                .put("/api/expeditions/1/2/remove")
                .then()
                .statusCode(200)
                .body("name", equalTo("Expedition 1"));
    }

    @Test
    void startExpedition_shouldReturnOk() {
        when(expeditionService.startExpedition(anyLong()))
                .thenReturn(new ExpeditionResponseDto(1L, "Expedition 1", "Desc", Difficulty.EASY, ExpeditionStatus.CREATED, null, null));

        given()
                .when()
                .put("/api/expeditions/1/start")
                .then()
                .statusCode(200)
                .body("name", equalTo("Expedition 1"));
    }

    @Test
    void endExpedition_shouldReturnOk() {
        when(expeditionService.finishExpedition(anyLong()))
                .thenReturn(new ExpeditionResponseDto(1L, "Expedition 1", "Desc", Difficulty.EASY, ExpeditionStatus.CREATED, null, null));

        given()
                .when()
                .put("/api/expeditions/1/end")
                .then()
                .statusCode(200)
                .body("name", equalTo("Expedition 1"));
    }
}