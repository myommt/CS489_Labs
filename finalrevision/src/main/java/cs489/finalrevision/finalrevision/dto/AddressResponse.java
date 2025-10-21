package cs489.finalrevision.finalrevision.dto;

public record AddressResponse(
    long addressId,
    String street,
    String city,
    String state,
    String zipCode
) {

}
