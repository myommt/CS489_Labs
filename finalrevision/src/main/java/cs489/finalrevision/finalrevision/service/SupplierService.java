package cs489.finalrevision.finalrevision.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cs489.finalrevision.finalrevision.dto.AddressRequest;
import cs489.finalrevision.finalrevision.dto.AddressResponse;
import cs489.finalrevision.finalrevision.dto.MoneyRequest;
import cs489.finalrevision.finalrevision.dto.ProductRequest;
import cs489.finalrevision.finalrevision.dto.ProductResponse;
import cs489.finalrevision.finalrevision.dto.SupplierRequest;
import cs489.finalrevision.finalrevision.dto.SupplierResponse;
import cs489.finalrevision.finalrevision.exception.NotFoundException;
import cs489.finalrevision.finalrevision.model.Address;
import cs489.finalrevision.finalrevision.model.Money;
import cs489.finalrevision.finalrevision.model.Product;
import cs489.finalrevision.finalrevision.model.Supplier;
import cs489.finalrevision.finalrevision.repository.SupplierRepository;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
            .map(this::toSupplierResponse)
            .toList();
    }

    public SupplierResponse getSupplier(Long id) {
        Supplier s = supplierRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Supplier %d not found".formatted(id)));
        return toSupplierResponse(s);
    }

    public SupplierResponse createSupplier(SupplierRequest request) {
        Supplier s = new Supplier();
        s.setName(request.name());
        s.setContactNumber(request.contactNumber());
        s.setPrimaryAddress(toAddress(request.primaryAddress()));

        if (request.products() != null && !request.products().isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (ProductRequest pr : request.products()) {
                Product p = toProduct(pr);
                p.setSupplier(s);
                products.add(p);
            }
            s.setProducts(products);
        }

        Supplier saved = supplierRepository.save(s);
        return toSupplierResponse(saved);
    }

    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier s = supplierRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Supplier %d not found".formatted(id)));

        s.setName(request.name());
        s.setContactNumber(request.contactNumber());
        s.setPrimaryAddress(toAddress(request.primaryAddress()));

        if (request.products() != null) {
            Set<Product> products = new HashSet<>();
            for (ProductRequest pr : request.products()) {
                Product p = toProduct(pr);
                p.setSupplier(s);
                products.add(p);
            }
            s.setProducts(products);
        }

        Supplier saved = supplierRepository.save(s);
        return toSupplierResponse(saved);
    }

    public void deleteSupplier(Long id) {
        Supplier s = supplierRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Supplier %d not found".formatted(id)));
        supplierRepository.delete(s);
    }

    private SupplierResponse toSupplierResponse(Supplier supplier) {
        AddressResponse address = new AddressResponse(
            supplier.getPrimaryAddress().getAddressId(),
            supplier.getPrimaryAddress().getStreet(),
            supplier.getPrimaryAddress().getCity(),
            supplier.getPrimaryAddress().getState(),
            supplier.getPrimaryAddress().getZipCode()
        );

        List<ProductResponse> products = supplier.getProducts() == null
            ? List.<ProductResponse>of()
            : supplier.getProducts().stream()
                .map(p -> new ProductResponse(
                    p.getProductId(),
                    p.getProductNo(),
                    p.getName(),
                    p.getQuantityInStock(),
                    p.getUnitprice()
                ))
                .toList();

        return new SupplierResponse(
            supplier.getSupplierId(),
            supplier.getName(),
            supplier.getContactNumber(),
            address,
            products
        );
    }

    private Address toAddress(AddressRequest ar) {
        Address a = new Address();
        a.setStreet(ar.street());
        a.setCity(ar.city());
        a.setState(ar.state());
        a.setZipCode(ar.zipCode());
        return a;
    }

    private Product toProduct(ProductRequest pr) {
        Product p = new Product();
        p.setProductNo(pr.productNo());
        p.setName(pr.name());
        p.setDateSupplied(pr.dateSupplied());
        p.setQuantityInStock(pr.quantityInStock());
        p.setUnitprice(new Money(pr.unitprice().currency(), pr.unitprice().amount()));
        return p;
    }
}
