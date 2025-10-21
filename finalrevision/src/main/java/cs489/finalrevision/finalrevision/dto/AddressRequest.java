package cs489.finalrevision.finalrevision.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String zipCode
) {}
