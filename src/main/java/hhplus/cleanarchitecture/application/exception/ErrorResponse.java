package hhplus.cleanarchitecture.application.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
