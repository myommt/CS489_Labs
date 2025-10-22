package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record SurgeryLocationRequestDTO(
    String name,
    String contactNumber,
    AddressRequestDTO addressRequestDTO
) {

}