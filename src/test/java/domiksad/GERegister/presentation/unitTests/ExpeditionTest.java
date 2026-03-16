package domiksad.GERegister.presentation.unitTests;

import domiksad.GERegister.domain.exceptions.ExpeditionException;
import domiksad.GERegister.domain.expedition.Expedition;
import domiksad.GERegister.domain.hunter.Hunter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpeditionTest {
    private Hunter hunter;
    private Expedition expedition;

    @BeforeEach
    void setUp() {
        hunter = new Hunter();
        expedition = new Expedition();
    }

    @Test
    void shouldThrowException_whenAddingToInProgressExpedition(){
        expedition.addHunter(hunter);
        expedition.start();

        assertThrows(ExpeditionException.class, () -> {
            expedition.addHunter(new Hunter());
        });
    }

    @Test
    public void shouldThrowException_whenNoAssignedHunters(){
        assertThrows(ExpeditionException.class, () -> {
           expedition.start();
        });
    }

    @Test
    public void shouldThrowException_whenAlreadyStarted(){
        expedition.addHunter(hunter);
        expedition.start();
        assertThrows(ExpeditionException.class, () -> {
            expedition.start();
        });
    }

    @Test
    public void shouldThrowException_whenAssignedHunterIsOnExpedition(){
        expedition.addHunter(hunter);
        assertThrows(ExpeditionException.class, expedition::start);
    }

    @Test
    public void shouldThrowException_whenTryingToFinishNotStartedExpedition(){
        assertThrows(ExpeditionException.class, expedition::finish);
    }
}
