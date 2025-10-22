package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record AddressRequestDTO(
    String street,
    String city,
    String state,
    String zipcode
) {

}
