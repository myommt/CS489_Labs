package cs489.finalrevision.finalrevision.service;

import cs489.finalrevision.finalrevision.dto.ProductRequest;
import cs489.finalrevision.finalrevision.dto.ProductResponse;
import cs489.finalrevision.finalrevision.model.Product;
import cs489.finalrevision.finalrevision.model.Money;
import cs489.finalrevision.finalrevision.repository.ProductRepository;
import cs489.finalrevision.finalrevision.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setProductNo(request.productNo());
        product.setName(request.name());
        product.setDateSupplied(request.dateSupplied());
        product.setQuantityInStock(request.quantityInStock());
        product.setUnitprice(new Money(request.unitprice().currency(), request.unitprice().amount()));
        // Note: Supplier is not set from request, handle separately if needed
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        product.setProductNo(request.productNo());
        product.setName(request.name());
        product.setDateSupplied(request.dateSupplied());
        product.setQuantityInStock(request.quantityInStock());
        product.setUnitprice(new Money(request.unitprice().currency(), request.unitprice().amount()));
        // Note: Supplier is not set from request, handle separately if needed
        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
            product.getProductId(),
            product.getProductNo(),
            product.getName(),
            product.getQuantityInStock(),
            product.getUnitprice()
        );
    }
}

