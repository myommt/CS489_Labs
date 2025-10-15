package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record ErrorResponseDTO(
    String error,
    String message
) {
}