package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record SurgeryLocationResponseDTO(
    Integer surgeryLocationId,
    String name,
    String contactNumber,
    AddressResponseDTO location
) {
}