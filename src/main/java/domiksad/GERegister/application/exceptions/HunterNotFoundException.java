package domiksad.GERegister.application.exceptions;

import java.util.UUID;

public class HunterNotFoundException extends RuntimeException {
    public HunterNotFoundException(UUID id) {
        super("Hunter with id " + id.toString() + " not found");
    }
}
