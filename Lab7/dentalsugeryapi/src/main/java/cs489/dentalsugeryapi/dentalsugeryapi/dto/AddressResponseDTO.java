package cs489.dentalsugeryapi.dentalsugeryapi.dto;

public record AddressResponseDTO (
    Integer addressId,
    String street,
    String city,
    String state,
    String zipcode){

}
