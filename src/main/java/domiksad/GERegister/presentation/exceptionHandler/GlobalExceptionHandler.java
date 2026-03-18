package domiksad.GERegister.presentation.exceptionHandler;

import domiksad.GERegister.application.exceptions.ExpeditionNotFoundException;
import domiksad.GERegister.application.exceptions.HunterNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        HttpStatus code = HttpStatus.BAD_REQUEST;

        Map<String, String> fieldsErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                code.value(),
                "Validation failed",
                fieldsErrors.toString()
        );

        return new ResponseEntity<>(errorResponse, code);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        HttpStatus code = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                code.value(),
                "Parameter type mismatch",
                "Parameter '" + ex.getName() + "' must be of type " + ex.getRequiredType().getSimpleName()
        );

        return new ResponseEntity<>(errorResponse, code);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        logger.warning(ex.getMessage());

        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                code.value(),
                "Internal Server Error",
                ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, code);
    }

    @ExceptionHandler({HunterNotFoundException.class, ExpeditionNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleObjectNotFoundException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Object not found",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        logger.severe(ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
