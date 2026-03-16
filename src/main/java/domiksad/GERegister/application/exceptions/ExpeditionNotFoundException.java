package domiksad.GERegister.application.exceptions;

public class ExpeditionNotFoundException extends RuntimeException {
    public ExpeditionNotFoundException(Long id) {
        super("Expedition with id " + id + " not found");
    }
}
