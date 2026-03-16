package domiksad.GERegister.presentation.controller;

import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterRequestDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(HunterController.class)
class HunterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HunterService hunterService;

    @BeforeEach
    void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    void getAllHunters() {
        when(hunterService.getAllHunters())
                .thenReturn(List.of(new HunterResponseDto(1L, "Gon")));

        given()
                .when()
                .get("/api/hunters")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Gon"));
    }

    @Test
    void getHunterById() {
        when(hunterService.getHunterById(1L))
                .thenReturn(new HunterResponseDto(1L, "Gon"));

        given()
                .when()
                .get("/api/hunters/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Gon"));
    }

    @Test
    void getHuntersExpeditions() {
        when(hunterService.getHuntersExpeditions(1L))
                .thenReturn(List.of(new ExpeditionResponseDto(1L, "Exp", "Expedition", Difficulty.EASY, ExpeditionStatus.CREATED, null, null)));

        given()
                .when()
                .get("/api/hunters/1/expeditions")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Exp"));
    }

    @Test
    void createHunter() {
        HunterRequestDto hunter = new HunterRequestDto("Gerald");

        when(hunterService.createHunter(any(HunterRequestDto.class)))
                .thenReturn(new HunterResponseDto(1L, "Gerald"));

        given()
                .contentType("application/json")
                .body(hunter)
                .when()
                .post("/api/hunters")
                .then()
                .statusCode(201)
                .body("name", equalTo("Gerald"));
    }

    @Test
    void updateHunter() {
        HunterRequestDto hunter = new HunterRequestDto("Gerald");

        when(hunterService.update(eq(1L), any(HunterRequestDto.class)))
                .thenReturn(new HunterResponseDto(1L, "Gerald"));

        given()
                .contentType("application/json")
                .body(hunter)
                .when()
                .put("/api/hunters/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Gerald"));
    }

    @Test
    void deleteHunterById() {
        given()
                .when()
                .delete("/api/hunters/1")
                .then()
                .statusCode(204);
    }

    // other
    @Test
    void shouldReturnCorrectHunterStructure() {
        when(hunterService.getHunterById(1L))
                .thenReturn(new HunterResponseDto(1L, "Gon"));

        given()
                .when()
                .get("/api/hunters/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Gon"));
    }

    @Test
    void shouldReturnEmptyListWhenNoHunters() {
        when(hunterService.getAllHunters())
                .thenReturn(List.of());

        given()
                .when()
                .get("/api/hunters")
                .then()
                .statusCode(200)
                .body("$", equalTo(List.of()));
    }

    @Test
    void shouldCallServiceWhenDeletingHunter() {
        given()
                .when()
                .delete("/api/hunters/1")
                .then()
                .statusCode(204);

        verify(hunterService).deleteHunterById(1L);
    }

    // Errors
    @Test
    void shouldReturn400WhenNameIsEmpty() {

        HunterRequestDto hunter = new HunterRequestDto("");

        given()
                .contentType("application/json")
                .body(hunter)
                .when()
                .post("/api/hunters")
                .then()
                .statusCode(400);
    }
}