package domiksad.GERegister.presentation.controller;

import domiksad.GERegister.application.service.HunterService;
import domiksad.GERegister.domain.expedition.Difficulty;
import domiksad.GERegister.domain.expedition.ExpeditionStatus;
import domiksad.GERegister.presentation.dto.ExpeditionResponseDto;
import domiksad.GERegister.presentation.dto.HunterResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@WebMvcTest(HunterController.class)
class HunterControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    HunterService hunterService;

    @Test
    void getAllHunters() {
        when(hunterService.getAllHunters())
                .thenReturn(List.of(new HunterResponseDto(1L, "Gon")));

        given()
                .when()
                .get("/api/hunters/1")
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
                .get("/api/hunters/1")
                .then()
                .statusCode(200)
                .body("[0].name", equalTo("Exp"));
    }

    @Test
    void createHunter() {
    }

    @Test
    void updateHunter() {
    }

    @Test
    void deleteHunterByID() {
    }
}