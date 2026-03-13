package domiksad.GERegister.application.exceptions;

public class HunterNotFoundException extends RuntimeException {
    public HunterNotFoundException(Long id) {
        super("Hunter with id " + id + " not found");
    }
}
