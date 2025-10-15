package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record DentistRequestDTO(
    String firstName,
    String lastName,
    String contactNumber,
    String email,
    String specialization
) {

}