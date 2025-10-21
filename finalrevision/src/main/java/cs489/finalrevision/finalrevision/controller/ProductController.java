package cs489.finalrevision.finalrevision.controller;

import cs489.finalrevision.finalrevision.dto.ProductRequest;
import cs489.finalrevision.finalrevision.dto.ProductResponse;
import cs489.finalrevision.finalrevision.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
	}

	@PostMapping
	public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
		ProductResponse created = productService.createProduct(request);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
		ProductResponse updated = productService.updateProduct(id, request);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}

