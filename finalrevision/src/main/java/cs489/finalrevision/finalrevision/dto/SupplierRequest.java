package cs489.finalrevision.finalrevision.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SupplierRequest(
    @NotBlank String name,
    String contactNumber,
    @NotNull AddressRequest primaryAddress,
    List<ProductRequest> products
) {}
