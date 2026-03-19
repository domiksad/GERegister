package domiksad.GERegister.application.exceptions;

import java.util.UUID;

public class ExpeditionNotFoundException extends RuntimeException {
    public ExpeditionNotFoundException(UUID id) {
        super("Expedition with id " + id.toString() + " not found");
    }
}
