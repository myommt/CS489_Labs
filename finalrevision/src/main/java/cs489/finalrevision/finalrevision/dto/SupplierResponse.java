package cs489.finalrevision.finalrevision.dto;

import cs489.finalrevision.finalrevision.dto.AddressResponse;
import cs489.finalrevision.finalrevision.dto.ProductResponse;
import java.util.List;
public record SupplierResponse(
    long supplierId,
    String name,
    String contactNumber,
    AddressResponse primaryAddress,
    List<ProductResponse> products) {

    
}
