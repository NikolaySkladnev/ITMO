package backend.academy.linktracker.scrapper.adapter.in.rest;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperRestExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        "Конфликт состояния", "409", e.getClass().getSimpleName(), e.getMessage(), List.of()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (message.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponse(
                            "Ресурс не найден", "404", e.getClass().getSimpleName(), e.getMessage(), List.of()));
        }

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        "Некорректные параметры запроса",
                        "400",
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        List.of()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        BindException.class,
        HttpMessageNotReadableException.class,
        MismatchedInputException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        "Некорректные параметры запроса",
                        "400",
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        List.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleInternal(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        "Внутренняя ошибка сервера", "500", e.getClass().getSimpleName(), e.getMessage(), List.of()));
    }

    public record ApiErrorResponse(
            String description, String code, String exceptionName, String exceptionMessage, List<String> stacktrace) {}
}
